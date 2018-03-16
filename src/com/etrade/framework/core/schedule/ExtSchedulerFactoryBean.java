package com.etrade.framework.core.schedule;

import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.etrade.framework.core.schedule.entity.JobBeanCfg;
import com.etrade.framework.core.schedule.service.JobBeanCfgService;
import com.google.common.collect.Lists;

/**
 * ��չ��׼��SchedulerFactoryBean��ʵ�ֻ������ݿ����õ������������ʼ��
 */
public class ExtSchedulerFactoryBean extends SchedulerFactoryBean {

    private static Logger logger = LoggerFactory.getLogger(ExtSchedulerFactoryBean.class);

    private ConfigurableApplicationContext applicationContext;

    private JobBeanCfgService jobBeanCfgService;

    private Boolean runWithinCluster = Boolean.TRUE;

    public void setRunWithinCluster(Boolean runWithinCluster) {
        this.runWithinCluster = runWithinCluster;
    }

    public Boolean getRunWithinCluster() {
        return runWithinCluster;
    }

    public void setJobBeanCfgService(JobBeanCfgService jobBeanCfgService) {
        this.jobBeanCfgService = jobBeanCfgService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        super.setApplicationContext(applicationContext);
    }

    @Override
    protected void registerJobsAndTriggers() throws SchedulerException {
        logger.debug("Invoking registerJobsAndTriggers...");
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        List<JobBeanCfg> jobBeanCfgs = jobBeanCfgService.findAll();
        List<Trigger> allTriggers = Lists.newArrayList();

        List<Trigger> triggers = null;
        try {
            //���ڷ����ȡ�Ѿ���XML�ж����triggers����
            triggers = (List<Trigger>) FieldUtils.readField(this, "triggers", true);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }

        if (triggers == null) {
            triggers = Lists.newArrayList();
        } else {
            allTriggers.addAll(triggers);
        }

        for (JobBeanCfg jobBeanCfg : jobBeanCfgs) {
            // ֻ�����뵱ǰScheduler��Ⱥ����ģʽƥ�������
            if (jobBeanCfg.getRunWithinCluster() == null || !jobBeanCfg.getRunWithinCluster().equals(runWithinCluster)) {
                continue;
            }
            // ������ȫ������ΪJob��Trigger�������
            Class<?> jobClass = null;
            try {
                jobClass = Class.forName(jobBeanCfg.getJobClass());
            } catch (ClassNotFoundException e) {
                //�ݴ�������������ô������޷�����Ӧ��
                logger.error(e.getMessage(), e);
            }
            if (jobClass == null) {
                continue;
            }
            String jobName = jobClass.getName();

            boolean jobExists = false;
            for (Trigger trigger : triggers) {
                if (trigger.getJobKey().getName().equals(jobName)) {
                    jobExists = true;
                    break;
                }
            }
            if (jobExists) {
                logger.warn("WARN: Skipped dynamic  job [{}] due to exists static configuration.", jobName);
                continue;
            }

            logger.debug("Build and schedule dynamical job�� {}, CRON: {}", jobName,
                jobBeanCfg.getCronExpression());

            // Spring��̬����Job Bean
            BeanDefinitionBuilder bdbJobDetailBean = BeanDefinitionBuilder
                    .rootBeanDefinition(JobDetailFactoryBean.class);
            bdbJobDetailBean.addPropertyValue("jobClass", jobBeanCfg.getJobClass());
            bdbJobDetailBean.addPropertyValue("durability", true);
            beanFactory.registerBeanDefinition(jobName, bdbJobDetailBean.getBeanDefinition());

            // Spring��̬����Trigger Bean
            String triggerName = jobName + ".Trigger";
            JobDetail jobDetailBean = (JobDetail) beanFactory.getBean(jobName);
            BeanDefinitionBuilder bdbCronTriggerBean = BeanDefinitionBuilder
                    .rootBeanDefinition(CronTriggerFactoryBean.class);
            bdbCronTriggerBean.addPropertyValue("jobDetail", jobDetailBean);
            bdbCronTriggerBean.addPropertyValue("cronExpression", jobBeanCfg.getCronExpression());
            beanFactory.registerBeanDefinition(triggerName, bdbCronTriggerBean.getBeanDefinition());

            allTriggers.add((Trigger) beanFactory.getBean(triggerName));
        }

        this.setTriggers(allTriggers.toArray(new Trigger[] {}));
        super.registerJobsAndTriggers();

        // ��AutoStartup�趨�ļƻ������ʼ����Ϊ��ͣ״̬
        for (JobBeanCfg jobBeanCfg : jobBeanCfgs) {
            if (!jobBeanCfg.getAutoStartup()) {
                for (Trigger trigger : allTriggers) {
                    if (jobBeanCfg.getJobClass().equals(trigger.getJobKey().getName())) {
                        logger.debug("Setup trigger {} state to PAUSE", trigger.getKey().getName());
                        this.getScheduler().pauseTrigger(trigger.getKey());
                        break;
                    }
                }
            }
        }
    }

}
