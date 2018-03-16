package com.etrade.framework.core.bpm.web.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.etrade.framework.core.bpm.service.ActivitiService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ProcessInstanceController extends SimpleController {

    protected static Logger logger = LoggerFactory.getLogger(ProcessInstanceController.class);

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
    protected ActivitiService activitiService;

    public Map<String, String> getProcessDefinitions() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
        Map<String, String> datas = Maps.newHashMap();
        for (ProcessDefinition processDefinition : processDefinitions) {
            datas.put(processDefinition.getKey(), processDefinition.getName());
        }
        return datas;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public HttpHeaders findByPageRunning() {
        Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(getRequest());
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        String searchBusinessKey = getParameter("businessKey");
        if (StringUtils.isNotBlank(searchBusinessKey)) {
            processInstanceQuery.processInstanceBusinessKey(searchBusinessKey);
        }
        String processDefinitionKey = getParameter("processDefinitionKey");
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            processInstanceQuery.processDefinitionKey(processDefinitionKey);
        }
        List<ProcessInstance> processInstances = processInstanceQuery.orderByProcessInstanceId().asc()
                .listPage(pageable.getOffset(), pageable.getPageSize());
        List<Map<String, Object>> datas = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(processInstances)) {
            Set<String> processInstanceIds = Sets.newHashSet();
            for (ProcessInstance pi : processInstances) {
                processInstanceIds.add(pi.getId());
            }
            List<HistoricProcessInstance> historicProcessInstances = historyService
                    .createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
            for (ProcessInstance processInstance : processInstances) {
                ExecutionEntity executionEntity = (ExecutionEntity) processInstance;
                Map<String, Object> data = Maps.newHashMap();
                String businessKey = executionEntity.getBusinessKey();
                ProcessDefinition pd = repositoryService.getProcessDefinition(executionEntity.getProcessDefinitionId());
                for (HistoricProcessInstance hpi : historicProcessInstances) {
                    if (hpi.getId().equals(processInstance.getId())) {
                        data.put("startUserId", hpi.getStartUserId());
                        break;
                    }
                }
                data.put("id", executionEntity.getId());
                data.put("executionEntityId", executionEntity.getId());
                data.put("businessKey", businessKey);
                data.put("processDefinitionName", pd.getName());
                data.put("activityNames", activitiService.findActiveTaskNames(businessKey));
                datas.add(data);
            }
        }
        setModel(new PageImpl(datas, pageable, processInstanceQuery.count()));
        return buildDefaultHttpHeaders();
    }

    public HttpHeaders forceTerminal() {
        //ɾ��ʧ�ܵ�id�Ͷ�Ӧ��Ϣ��Map�ṹ���أ�������ǰ��������ʾ������ʾ�ͼ������������ɾ������
        Map<String, String> errorMessageMap = Maps.newLinkedHashMap();

        String[] ids = getParameterIds();
        for (String id : ids) {
            String msg = "Terminal processInstance[" + id + "]  by user " + AuthContextHolder.getAuthUserPin();
            logger.debug(msg);
            activitiService.deleteProcessInstanceByProcessInstanceId(id, msg);
        }
        int rejectSize = errorMessageMap.size();
        if (rejectSize == 0) {
            setModel(OperationResult.buildSuccessResult("ǿ�ƽ�������ʵ��ѡȡ��¼:" + ids.length + "��"));
        } else {
            if (rejectSize == ids.length) {
                setModel(OperationResult.buildFailureResult("ǿ�ƽ�������ʵ������ʧ��", errorMessageMap));
            } else {
                setModel(OperationResult.buildWarningResult("ǿ�ƽ�������ʵ�������Ѵ���. �ɹ�:"
                                                            + (ids.length - rejectSize) + "��"
                                                            + ",ʧ��:" + rejectSize + "��",
                    errorMessageMap));
            }
        }
        return buildDefaultHttpHeaders();
    }
}
