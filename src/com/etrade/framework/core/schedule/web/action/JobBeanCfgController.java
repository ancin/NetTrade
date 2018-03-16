package com.etrade.framework.core.schedule.web.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.rest.HttpHeaders;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.schedule.ExtSchedulerFactoryBean;
import com.etrade.framework.core.schedule.entity.JobBeanCfg;
import com.etrade.framework.core.schedule.service.JobBeanCfgService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@MetaData(value = "��ʱ�������ù���")
public class JobBeanCfgController extends BaseController<JobBeanCfg, String> {

    @Autowired
    private JobBeanCfgService jobBeanCfgService;

    @Override
    protected BaseService<JobBeanCfg, String> getEntityService() {
        return jobBeanCfgService;
    }

    @Override
    protected void checkEntityAclPermission(JobBeanCfg entity) {
        // Nothing to do
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData(value = "Trigger�б�")
    public HttpHeaders triggers() throws IllegalAccessException, SchedulerException {
        List<Map<String, Object>> triggerDatas = Lists.newArrayList();

        Map<Trigger, SchedulerFactoryBean> allTriggers = jobBeanCfgService.findAllTriggers();
        for (Map.Entry<Trigger, SchedulerFactoryBean> me : allTriggers.entrySet()) {
            Trigger trigger = me.getKey();
            ExtSchedulerFactoryBean schedulerFactoryBean = (ExtSchedulerFactoryBean) me.getValue();
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            Map<String, Object> triggerMap = Maps.newHashMap();
            triggerMap.put("id", trigger.getJobKey().getName());
            triggerMap.put("jobName", trigger.getJobKey().getName());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                triggerMap.put("cronExpression", cronTrigger.getCronExpression());
                triggerMap.put("previousFireTime", cronTrigger.getPreviousFireTime());
                triggerMap.put("nextFireTime", cronTrigger.getNextFireTime());
            }
            triggerMap.put("stateLabel", scheduler.getTriggerState(trigger.getKey()));
            triggerMap.put("runWithinCluster", schedulerFactoryBean.getRunWithinCluster());
            triggerDatas.add(triggerMap);
        }

        setModel(buildPageResultFromList(triggerDatas));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "���üƻ�����״̬")
    public HttpHeaders doStateTrigger() throws SchedulerException {
        String[] ids = getParameterIds();
        String state = this.getRequiredParameter("state");
        Map<Trigger, SchedulerFactoryBean> allTriggers = jobBeanCfgService.findAllTriggers();
        for (String id : ids) {
            for (Map.Entry<Trigger, SchedulerFactoryBean> me : allTriggers.entrySet()) {
                Trigger trigger = me.getKey();
                if (trigger.getJobKey().getName().equals(id)) {
                    if (state.equals("pause")) {
                        me.getValue().getScheduler().pauseTrigger(trigger.getKey());
                    } else if (state.equals("resume")) {
                        me.getValue().getScheduler().resumeTrigger(trigger.getKey());
                    } else {
                        throw new UnsupportedOperationException("state parameter [" + state
                                + "] not in [pause, resume]");
                    }
                    break;
                }
            }
        }
        setModel(OperationResult.buildSuccessResult("����״̬���²������"));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "����ִ�мƻ�����")
    public HttpHeaders doRunTrigger() throws SchedulerException {
        String[] ids = getParameterIds();
        Map<Trigger, SchedulerFactoryBean> allTriggers = jobBeanCfgService.findAllTriggers();
        for (String id : ids) {
            for (Map.Entry<Trigger, SchedulerFactoryBean> me : allTriggers.entrySet()) {
                Trigger trigger = me.getKey();
                if (trigger.getJobKey().getName().equals(id)) {
                    me.getValue().getScheduler().triggerJob(trigger.getJobKey());
                    break;
                }
            }
        }
        setModel(OperationResult.buildSuccessResult("����ִ�мƻ�������ҵ�������"));
        return buildDefaultHttpHeaders();
    }
}