package com.etrade.biz.schedule;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.schedule.BaseQuartzJobBean;

/**
 * 商品数据监控预警任务
 */
public class CommodityMonitorJob extends BaseQuartzJobBean {

    private final static Logger logger = LoggerFactory.getLogger(CommodityMonitorJob.class);

    @Override
    protected void executeInternalBiz(JobExecutionContext context) {
        logger.debug("Just Mock: Monitor commodity data...");
    }

}
