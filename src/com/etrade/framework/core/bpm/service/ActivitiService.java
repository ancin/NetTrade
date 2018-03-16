package com.etrade.framework.core.bpm.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.etrade.framework.core.bpm.BpmTrackable;
import com.etrade.framework.core.security.AuthContextHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
@Transactional
public class ActivitiService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String BPM_ENTITY_VAR_NAME = "entity";

    public static final String BPM_INITIATOR_VAR_NAME = "initiator";

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired(required = false)
    protected RuntimeService runtimeService;

    @Autowired(required = false)
    protected TaskService taskService;

    @Autowired(required = false)
    private FormService formService;

    @Autowired(required = false)
    protected RepositoryService repositoryService;

    @Autowired(required = false)
    protected IdentityService identityService;

    @Autowired(required = false)
    protected HistoryService historyService;

    @Autowired(required = false)
    ProcessEngineFactoryBean processEngine;

    /**
     * ����ҵ��������������ʵ������ͼ
     * @param bizKey
     * @return
     */
    public InputStream buildProcessImageByBizKey(String bizKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(bizKey).singleResult();
        return buildProcessImageByProcessInstance(processInstance);
    }

    /**
     * ����ProcessInstanceIdɾ������
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByProcessInstanceId(String processInstanceId, String message) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        deleteProcessInstanceByProcessInstance(processInstance, message);
    }

    /**
     * ����ҵ������ɾ������
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByEntity(BpmTrackable entity) {
        entity.setActiveTaskName("END");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(entity.getBpmBusinessKey()).singleResult();
        deleteProcessInstanceByProcessInstance(processInstance,
                "Casecade by entity delete [" + entity.getBpmBusinessKey() + "]");
    }

    /**
     * ����ҵ������ɾ������
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByBizKey(String bizKey, String message) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(bizKey).singleResult();
        deleteProcessInstanceByProcessInstance(processInstance, message);
    }

    /**
     * ����processInstanceɾ������
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByProcessInstance(ProcessInstance processInstance, String message) {
        if (processInstance != null) {
            //try-catch�����������ڸ��°汾���������������ݻ�ȡ������ʧ�ܵ����޷�ɾ��
            try {
                Object val = runtimeService.getVariable(processInstance.getProcessInstanceId(), BPM_ENTITY_VAR_NAME);
                if (val != null && val instanceof BpmTrackable) {
                    BpmTrackable entity = (BpmTrackable) val;
                    entity.setActiveTaskName("END");
                    entityManager.persist(entity);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            identityService.setAuthenticatedUserId(AuthContextHolder.getAuthUserPin());
            runtimeService.deleteProcessInstance(processInstance.getId(), message);
        }
    }

    /**
     * ��������ʵ��ID��������ʵ������ͼ
     * @param processInstanceId
     * @return
     */
    public InputStream buildProcessImageByProcessInstanceId(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        return buildProcessImageByProcessInstance(processInstance);
    }

    private InputStream buildProcessImageByProcessInstance(ProcessInstance processInstance) {
        if (processInstance == null) {
            return null;
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processInstance.getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstance.getProcessInstanceId());
        // ʹ��springע��������ʹ����������д���
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        List<String> highLightedFlows = getHighLightedFlows(processDefinition, processInstance.getProcessInstanceId());

        InputStream imageStream = ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds,
                highLightedFlows);
        return imageStream;
    }

    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {
        List<String> historicActivityInstanceList = new ArrayList<String>();
        List<String> highLightedFlows = new ArrayList<String>();

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

        for (HistoricActivityInstance hai : historicActivityInstances) {
            historicActivityInstanceList.add(hai.getActivityId());
        }

        // add current activities to list
        List<String> highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
        historicActivityInstanceList.addAll(highLightedActivities);

        // activities and their sequence-flows
        getHighLightedFlows(processDefinition.getActivities(), highLightedFlows, historicActivityInstanceList);

        return highLightedFlows;
    }

    private void getHighLightedFlows(List<ActivityImpl> activityList, List<String> highLightedFlows,
            List<String> historicActivityInstanceList) {
        for (ActivityImpl activity : activityList) {
            if (activity.getProperty("type").equals("subProcess")) {
                // get flows for the subProcess
                getHighLightedFlows(activity.getActivities(), highLightedFlows, historicActivityInstanceList);
            }

            if (historicActivityInstanceList.contains(activity.getId())) {
                List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitionList) {
                    String destinationFlowId = pvmTransition.getDestination().getId();
                    if (historicActivityInstanceList.contains(destinationFlowId)) {
                        highLightedFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
    }

    /**
     * ��ѯҵ�����ǰ���������
     * @param bizKey �������̵�ҵ������
     * @return
     */
    public String findActiveTaskNames(String bizKey) {
        Assert.notNull(bizKey);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(bizKey).singleResult();
        //��������ᣬֱ�ӷ���null
        if (processInstance == null) {
            return "END";
        }
        List<String> ids = runtimeService.getActiveActivityIds(processInstance.getId());
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance
                .getProcessDefinitionId());
        List<ActivityImpl> activityImpls = pde.getActivities();
        List<String> activeActs = Lists.newArrayList();
        for (ActivityImpl activityImpl : activityImpls) {
            if (ids.contains(activityImpl.getId())) {
                activeActs.add(ObjectUtils.toString(activityImpl.getProperty("name")));
            }
        }
        return StringUtils.join(activeActs, ",");
    }

    /**  
     * ���ݵ�ǰ����ID����ѯ���Բ��ص�����ڵ�  
     *   
     * @param taskId ��ǰ����ID  
     */
    public List<ActivityImpl> findBackActivities(String taskId) {
        List<ActivityImpl> rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId, null),
                new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
        return reverList(rtnList);
    }

    /**  
     * ����ָ��Ŀ���ڵ�  
     *   
     * @param taskId ��ǰ����ID  
     * @param activityId ���ؽڵ�ID  
     * @param variables ���̴洢����  
     * @throws Exception  
     */
    public void backActivity(String taskId, String activityId, Map<String, Object> variables) {
        Assert.notNull(activityId, "Back target process activity id required");

        // �������в�������ڵ㣬ͬʱ����    
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId)
                .getTaskDefinitionKey());
        for (Task task : taskList) {
            commitProcess(task.getId(), variables, activityId);
        }
    }

    /**  
     * ����ָ��Ŀ���ڵ�  
     *   
     * @param taskId ��ǰ����ID  
     * @param activityId ���ؽڵ�ID  
     * @throws Exception  
     */
    public void backActivity(String taskId, String activityId) {
        backActivity(taskId, activityId, null);
    }

    /**  
     * ���ָ����ڵ�����  
     *   
     * @param activityImpl ��ڵ�  
     * @return �ڵ����򼯺�  
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // �洢��ǰ�ڵ�����������ʱ����    
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // ��ȡ��ǰ�ڵ��������򣬴洢����ʱ������Ȼ�����    
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**  
     * @param taskId ��ǰ����ID  
     * @param variables ���̱���  
     * @param activityId ����ת��ִ������ڵ�ID<br>�˲���Ϊ�գ�Ĭ��Ϊ�ύ����  
     * @throws Exception  
     */
    private void commitProcess(String taskId, Map<String, Object> variables, String activityId) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // ��ת�ڵ�Ϊ�գ�Ĭ���ύ����    
        if (StringUtils.isEmpty(activityId)) {
            taskService.complete(taskId, variables);
        } else {// ����ת�����    
            turnTransition(taskId, activityId, variables);
        }
    }

    /**  
     * ��ֹ����(��Ȩ��ֱ������ͨ����)  
     *   
     * @param taskId  
     */
    public void endProcess(String taskId) {
        ActivityImpl endActivity = findActivitiImpl(taskId, "end");
        commitProcess(taskId, null, endActivity.getId());
    }

    /**  
     * �����������񼯺ϣ���ѯ���һ�ε���������ڵ�  
     *   
     * @param processInstance ����ʵ��  
     * @param tempList �������񼯺�  
     * @return  
     */
    private ActivityImpl filterNewestActivity(ProcessInstance processInstance, List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**  
     * ��������ID�ͽڵ�ID��ȡ��ڵ� <br>  
     *   
     * @param taskId ����ID  
     * @param activityId ��ڵ�ID<br>���Ϊnull��""����Ĭ�ϲ�ѯ��ǰ��ڵ� <br>���Ϊ"end"�����ѯ�����ڵ� <br>  
     *   
     * @return  
     * @throws Exception  
     */
    private ActivityImpl findActivitiImpl(String taskId, String activityId) {
        // ȡ�����̶���    
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // ��ȡ��ǰ��ڵ�ID    
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }

        // �������̶��壬��ȡ������ʵ���Ľ����ڵ�    
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // ���ݽڵ�ID����ȡ��Ӧ�Ļ�ڵ�    
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);

        return activityImpl;
    }

    /**  
     * ��ѯָ������ڵ�����¼�¼  
     *   
     * @param processInstance ����ʵ��  
     * @param activityId  
     * @return  
     */
    private HistoricActivityInstance findHistoricUserTask(ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // ��ѯ��ǰ����ʵ��������������ʷ�ڵ�    
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .activityType("userTask").processInstanceId(processInstance.getId()).activityId(activityId).finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**  
     * ���ݵ�ǰ�ڵ㣬��ѯ��������Ƿ�Ϊ�����յ㣬���Ϊ�����յ㣬��ƴװ��Ӧ�Ĳ������ID  
     *   
     * @param activityImpl ��ǰ�ڵ�  
     * @return  
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// ����·��    
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_")) + "_start";
                }
            }
        }
        return null;
    }

    /**  
     * ��������ID��ȡ���̶���  
     *   
     * @param taskId  
     *            ����ID  
     * @return  
     * @throws Exception  
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) {
        // ȡ�����̶���    
        return (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId).getProcessDefinitionId());
    }

    /**  
     * ��������ID��ȡ��Ӧ������ʵ��  
     *   
     * @param taskId ����ID  
     * @return  
     * @throws Exception  
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId) {
        // �ҵ�����ʵ��    
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(findTaskById(taskId).getProcessInstanceId()).singleResult();
    }

    /**  
     * ��������ID�������ʵ��  
     *   
     * @param taskId ����ID  
     * @return  
     * @throws Exception  
     */
    private TaskEntity findTaskById(String taskId) {
        return (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**  
     * ��������ʵ��ID������keyֵ��ѯ����ͬ�����񼯺�  
     *   
     * @param processInstanceId  
     * @param key  
     * @return  
     */
    private List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(key).list();
    }

    /**  
     * ����ѭ���������ṹ����ѯ��ǰ�ڵ�ɲ��ص�����ڵ�  
     *   
     * @param taskId ��ǰ����ID  
     * @param currActivity ��ǰ��ڵ�  
     * @param rtnList �洢���˽ڵ㼯��  
     * @param tempList ��ʱ�洢�ڵ㼯�ϣ��洢һ�ε��������е�ͬ��userTask�ڵ㣩  
     * @return ���˽ڵ㼯��  
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId, ActivityImpl currActivity,
            List<ActivityImpl> rtnList, List<ActivityImpl> tempList) {
        // ��ѯ���̶��壬�����������ṹ    
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // ��ǰ�ڵ��������Դ    
        List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();
        // ������֧�ڵ㼯�ϣ�userTask�ڵ������ϣ����������˼��ϣ���ѯ������֧��Ӧ��userTask�ڵ�    
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // ���нڵ㼯�ϣ�userTask�ڵ������ϣ����������˼��ϣ���ѯ���нڵ��Ӧ��userTask�ڵ�    
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // ������ǰ�ڵ���������·��    
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**  
             * ���нڵ�����Ҫ��<br>  
             * ����ɶԳ��֣���Ҫ��ֱ����ýڵ�IDΪ:XXX_start(��ʼ)��XXX_end(����)  
             */
            if ("parallelGateway".equals(type)) {// ����·��    
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// ������㣬ֹͣ�ݹ�    
                    return rtnList;
                } else {// �����յ㣬��ʱ�洢�˽ڵ㣬����ѭ���������������ϣ���ѯ��Ӧ��userTask�ڵ�    
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// ��ʼ�ڵ㣬ֹͣ�ݹ�    
                return rtnList;
            } else if ("userTask".equals(type)) {// �û�����    
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// ��֧·�ߣ���ʱ�洢�˽ڵ㣬����ѭ���������������ϣ���ѯ��Ӧ��userTask�ڵ�    
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**  
         * ����������֧���ϣ���ѯ��Ӧ��userTask�ڵ�  
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**  
         * �������м��ϣ���ѯ��Ӧ��userTask�ڵ�  
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**  
         * ����ͬ��userTask���ϣ�������������Ľڵ�  
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // ��ѯ��ǰ�ڵ�������Ƿ�Ϊ�����յ㣬����ȡ�������ID    
            String id = findParallelGatewayId(currActivity);
            if (StringUtils.isEmpty(id)) {// �������IDΪ�գ��˽ڵ������ǲ����յ㣬���ϲ����������洢�˽ڵ�    
                rtnList.add(currActivity);
            } else {// ���ݲ������ID��ѯ��ǰ�ڵ㣬Ȼ�������ѯ���Ӧ��userTask����ڵ�    
                currActivity = findActivitiImpl(taskId, id);
            }

            // ��ձ��ε�����ʱ����    
            tempList.clear();
            // ִ���´ε���    
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }

    /**  
     * ��ԭָ����ڵ�����  
     *   
     * @param activityImpl ��ڵ�  
     * @param oriPvmTransitionList ԭ�нڵ����򼯺�  
     */
    private void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
        // �����������    
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        pvmTransitionList.clear();
        // ��ԭ��ǰ����    
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**  
     * ��������list���ϣ����ڲ��ؽڵ㰴˳����ʾ  
     *   
     * @param list  
     * @return  
     */
    private List<ActivityImpl> reverList(List<ActivityImpl> list) {
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
        // ���ڵ��������ظ����ݣ��ų��ظ�    
        for (int i = list.size(); i > 0; i--) {
            if (!rtnList.contains(list.get(i - 1)))
                rtnList.add(list.get(i - 1));
        }
        return rtnList;
    }

    /**  
     * ����ת�����  
     *   
     * @param taskId ��ǰ����ID  
     * @param activityId Ŀ��ڵ�����ID  
     * @param variables ���̱���  
     * @throws Exception  
     */
    private void turnTransition(String taskId, String activityId, Map<String, Object> variables) {
        // ��ǰ�ڵ�    
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // ��յ�ǰ����    
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // ����������    
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // Ŀ��ڵ�    
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // �����������Ŀ��ڵ�    
        newTransition.setDestination(pointActivity);

        // ִ��ת������    
        taskService.complete(taskId, variables);
        // ɾ��Ŀ��ڵ�������    
        pointActivity.getIncomingTransitions().remove(newTransition);

        // ��ԭ��ǰ����    
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * �������̶���Key�������°汾����ʵ��
     * @param processDefinitionKey
     * @param businessKey
     * @param variables
     * @return
     */
    public void startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthUserPin());
        runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    /**
     * �������̶���Key�������°汾����ʵ��
     * @param processDefinitionKey
     * @param businessKey
     * @param entity
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void startProcessInstanceByKey(String processDefinitionKey, BpmTrackable entity) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthUserPin());
        Map variables = Maps.newHashMap();
        variables.put(BPM_ENTITY_VAR_NAME, entity);
        runtimeService.startProcessInstanceByKey(processDefinitionKey, entity.getBpmBusinessKey(), variables);
        String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
        entity.setActiveTaskName(activeTaskNames);
    }

    /**
     * �������
     * @param taskId
     * @param variables
     * @return
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthUserPin());
        if (variables != null && variables.size() > 0) {
            taskService.setVariablesLocal(taskId, variables);
        }
        BpmTrackable entity = (BpmTrackable) taskService.getVariable(taskId, BPM_ENTITY_VAR_NAME);
        taskService.complete(taskId, variables);
        if (entity != null) {
            entity = entityManager.find(entity.getClass(), entity.getId());
            String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
            entity.setActiveTaskName(activeTaskNames);
            entityManager.persist(entity);
        }
    }

    /**
     * ���ڱ������������
     * @param taskId
     * @param formProperties
     * @return
     */
    public void submitTaskFormData(String taskId, Map<String, String> formProperties) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthUserPin());
        BpmTrackable entity = (BpmTrackable) taskService.getVariable(taskId, BPM_ENTITY_VAR_NAME);
        formService.submitTaskFormData(taskId, formProperties);
        if (entity != null) {
            entity = entityManager.find(entity.getClass(), entity.getId());
            String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
            entity.setActiveTaskName(activeTaskNames);
            entityManager.persist(entity);
        }
    }
}
