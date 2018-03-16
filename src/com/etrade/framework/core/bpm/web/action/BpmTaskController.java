package com.etrade.framework.core.bpm.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.form.TaskFormDataImpl;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.bpm.service.ActivitiService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Maps;

public class BpmTaskController extends SimpleController {

    private final Logger logger = LoggerFactory.getLogger(BpmTaskController.class);

    private final static String DYNA_FORM_KEY = "/bpm/bpm-task!dynaForm";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private FormService formService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    protected ActivitiService activitiService;

    @Autowired
    protected UserService userService;

    @Autowired
    private DynamicConfigService dynamicConfigService;

    private Map<String, Object> packageTaskInfo(Task task) {

        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        Map<String, Object> singleTask = new HashMap<String, Object>();
        singleTask.put("id", task.getId());
        singleTask.put("name", task.getName());
        singleTask.put("createTime", task.getCreateTime());
        singleTask.put("pdname", processDefinition.getName());
        singleTask.put("pdversion", processDefinition.getVersion());
        singleTask.put("pid", task.getProcessInstanceId());
        singleTask.put("bizKey", processInstance.getBusinessKey());

        try {
            Map<String, Object> variables = taskService.getVariables(task.getId());
            singleTask.put("initiator", variables.get(ActivitiService.BPM_INITIATOR_VAR_NAME));
        } catch (Exception e) {
            //�����������ʵ������ѯ�쳣����ҳ����ʾ����ҳ��
            logger.error(e.getMessage(), e);
        }

        return singleTask;
    }

    @MetaData(value = "�û����������б�")
    @SecurityControlIgnore
    public HttpHeaders userTasks() {
        // �Ѿ�ǩ�յ�����
        String userpin = AuthContextHolder.getAuthUserPin();

        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();

        List<Task> todoList = taskService.createTaskQuery().taskAssignee(userpin).active().orderByTaskCreateTime()
                .desc().list();
        for (Task task : todoList) {
            Map<String, Object> singleTask = packageTaskInfo(task);
            singleTask.put("candidate", false);
            tasks.add(singleTask);
        }

        // �ȴ�ǩ�յ�����
        List<Task> toClaimList = taskService.createTaskQuery().taskCandidateUser(userpin).active()
                .orderByTaskCreateTime().desc().list();
        for (Task task : toClaimList) {
            Map<String, Object> singleTask = packageTaskInfo(task);
            singleTask.put("candidate", true);
            tasks.add(singleTask);
        }

        HttpServletRequest request = ServletActionContext.getRequest();
        request.setAttribute("tasks", tasks);

        return new DefaultHttpHeaders("list").disableCaching();
    }

    /**
     * �������������ѯTaskʵ��������׷�ӵ�ǰ��¼�û�����ȷ��������ַǷ����ݷ���
     * @param taskId
     * @param candidate
     * @return
     */
    private Task getUserTaskByRequest() {
        String userpin = AuthContextHolder.getAuthUserPin();
        HttpServletRequest request = ServletActionContext.getRequest();
        String taskId = request.getParameter("taskId");
        Assert.notNull(taskId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task.getAssignee() == null) {
            task = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(userpin).singleResult();
            Assert.notNull(task, "BPM Task access denied");
        } else {
            Assert.isTrue(task.getAssignee().equals(userpin), "Task assignee not match");
        }
        return task;
    }

    @MetaData(value = "������ʾ")
    public HttpHeaders show() {
        HttpServletRequest request = ServletActionContext.getRequest();
        Task task = getUserTaskByRequest();
        request.setAttribute("task", task);

        TaskFormDataImpl taskFormData = (TaskFormDataImpl) formService.getTaskFormData(task.getId());
        String formKey = taskFormData.getFormKey();
        if (StringUtils.isBlank(formKey)) {
            formKey = DYNA_FORM_KEY + "?id=" + task.getId();
        } else {
            formKey = formKey + (formKey.indexOf("?") > -1 ? "&" : "?") + "taskId=" + task.getId();
        }
        request.setAttribute("formKey", formKey);
        return new DefaultHttpHeaders("show").disableCaching();
    }

    @MetaData(value = "��������嵥��ʾ")
    public HttpHeaders variables() {
        HttpServletRequest request = ServletActionContext.getRequest();
        Task task = getUserTaskByRequest();
        Map<String, Object> variables = taskService.getVariables(task.getId());
        request.setAttribute("variables", variables);
        return new DefaultHttpHeaders("variables").disableCaching();
    }

    public HttpHeaders dynaForm() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String taskId = request.getParameter("id");
        TaskFormDataImpl taskFormData = (TaskFormDataImpl) formService.getTaskFormData(taskId);
        request.setAttribute("taskFormData", taskFormData);
        return new DefaultHttpHeaders("form").disableCaching();
    }

    @MetaData(value = "����ǩ��")
    public HttpHeaders claim() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String userpin = AuthContextHolder.getAuthUserPin();
        String taskId = request.getParameter("id");
        taskService.claim(taskId, userpin);
        model = OperationResult.buildSuccessResult("����ǩ�ճɹ�", userpin);
        return new DefaultHttpHeaders().disableCaching();
    }

    @MetaData(value = "����ת��")
    public HttpHeaders trasfer() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String taskId = request.getParameter("id");
        String assignee = request.getParameter("assignee");
        User user = userService.findBySigninid(assignee);
        if (user == null) {
            model = OperationResult.buildFailureResult("δ�ҵ�ƥ���¼�˺ţ�" + assignee);
        } else {
            taskService.setAssignee(taskId, assignee);
            model = OperationResult.buildSuccessResult("����ת������ɹ�");
        }
        return new DefaultHttpHeaders().disableCaching();
    }

    public HttpHeaders complete() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String taskId = request.getParameter("id");

        Map<String, String> formProperties = new HashMap<String, String>();
        // ��request�ж�ȡ����Ȼ��ת��
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            // fp_����˼��form paremeter
            if (StringUtils.defaultString(key).startsWith("fp_")) {
                formProperties.put(key.split("_")[1], entry.getValue()[0]);
            }
        }
        activitiService.submitTaskFormData(taskId, formProperties);
        model = OperationResult.buildSuccessResult("������ɹ�");
        return new DefaultHttpHeaders().disableCaching();
    }

    @MetaData(value = "������˱���ʾ")
    public HttpHeaders backActivity() {
        HttpServletRequest request = ServletActionContext.getRequest();
        Task task = getUserTaskByRequest();
        List<ActivityImpl> activityImpls = activitiService.findBackActivities(task.getId());
        Map<String, String> dataMap = Maps.newLinkedHashMap();
        for (ActivityImpl activityImpl : activityImpls) {
            dataMap.put(activityImpl.getId(), ObjectUtils.toString(activityImpl.getProperty("name")));
        }
        request.setAttribute("task", task);
        request.setAttribute("backActivities", dataMap);
        return new DefaultHttpHeaders("backActivity").disableCaching();
    }

    @MetaData(value = "������˴���")
    public HttpHeaders doBackActivity() {
        Assert.isTrue(isProcessBackSupport(), "������˹��ܲ�����");
        HttpServletRequest request = ServletActionContext.getRequest();
        Task task = getUserTaskByRequest();
        String activityId = request.getParameter("activityId");
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("backActivityExplain", request.getParameter("backActivityExplain"));
        activitiService.backActivity(task.getId(), activityId, variables);
        model = OperationResult.buildSuccessResult("����ʵ����ת������ɹ�");
        return new DefaultHttpHeaders().disableCaching();
    }

    /**
     * ��������������ɻ��˹���֧�ֿ���
     * ���������ת���̴���ҵ�����ݽ����������ɻ��˹��ܺܿ��ܵ��������ظ������һ�µ��������
     * ��˳������̺�ҵ���ϴ�����Ǿ�����ϸ�����ʵ�֣�����ر����ɻ��˹��ܻ��й���Ա��ʱ��Ԥ����
     * ��ѡֵ˵����disabled=ȫ�ֹر�; enable=ȫ������; admin=ֻ��ROLE_ADMIN��ɫ�û����й���Ȩ��
     * @return
     */
    public boolean isProcessBackSupport() {
        String back = dynamicConfigService.getString("cfg.bpm.process.back.support", "admin");
        if (back.equalsIgnoreCase("enable")) {
            return true;
        } else if (back.equalsIgnoreCase("admin")) {
            return AuthContextHolder.isAdminUser();
        }
        return false;
    }

    /**
     * ������������Ƿ���ʾ���̱�����Ϣ����
     * ��ѡֵ˵����disabled=ȫ�ֹر�; enable=ȫ������; admin=ֻ��ROLE_ADMIN��ɫ�û����й���Ȩ��
     * @return
     */
    public boolean isShowProcessVariables() {
        String back = dynamicConfigService.getString("cfg.bpm.process.variables.show", "admin");
        if (back.equalsIgnoreCase("enable")) {
            return true;
        } else if (back.equalsIgnoreCase("admin")) {
            return AuthContextHolder.isAdminUser();
        }
        return false;
    }
}
