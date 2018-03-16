package com.etrade.framework.core.schedule.job;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.schedule.BaseQuartzJobBean;

/**
 * ���������ͳ�����񣨵��͵�Quartz�������Ǽ�Ⱥ������ģʽ������
 */
public class ServerMonitorJob extends BaseQuartzJobBean {

    private final static Logger logger = LoggerFactory.getLogger(ServerMonitorJob.class);

    @Override
    protected void executeInternalBiz(JobExecutionContext context) {
        logger.debug("Just Mock: Monitor current server information, such as CPU, Memery...");
    }

}
