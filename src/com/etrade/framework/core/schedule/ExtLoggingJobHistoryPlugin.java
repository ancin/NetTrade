package com.etrade.framework.core.schedule;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.plugins.history.LoggingJobHistoryPlugin;

import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.schedule.entity.JobBeanCfg;
import com.etrade.framework.core.schedule.entity.JobRunHist;
import com.etrade.framework.core.schedule.service.JobBeanCfgService;
import com.etrade.framework.core.schedule.service.JobRunHistService;
import com.google.common.collect.Sets;

/**
 * ��չʵ��LoggingJobHistoryPluginԼ���ӿ�
 * ת��Quartz�ṩ����ؽӿ�����ΪScheduleJobRunHist���󲢵��ö�Ӧ��Service�ӿڰ�����д�����ݿ����
 */
public class ExtLoggingJobHistoryPlugin extends LoggingJobHistoryPlugin {

	public static Set<Trigger> UNCONFIG_TRIGGERS = Sets.newHashSet();

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		super.jobWasExecuted(context, jobException);

		Trigger trigger = context.getTrigger();
		if (!UNCONFIG_TRIGGERS.contains(trigger)) {
			JobBeanCfgService jobBeanCfgService = SpringContextHolder.getBean(JobBeanCfgService.class);
			JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(trigger.getJobKey().getName());
			if (jobBeanCfg == null) {
				UNCONFIG_TRIGGERS.add(trigger);
			} else {
				if (!jobBeanCfg.getLogRunHist()) {
					return;
				}
			}
		}

		JobRunHistService jobRunHistService = SpringContextHolder.getBean(JobRunHistService.class);
		JobRunHist jobRunHist = new JobRunHist();
		try {
			jobRunHist.setNodeId(InetAddress.getLocalHost().toString());
		} catch (UnknownHostException e) {
			jobRunHist.setNodeId("U/A");
		}
		jobRunHist.setTriggerGroup(trigger.getKey().getGroup());
		jobRunHist.setTriggerName(trigger.getKey().getName());
		jobRunHist.setJobClass(context.getJobDetail().getJobClass().getName());
		jobRunHist.setJobName(context.getJobDetail().getKey().getName());
		jobRunHist.setJobGroup(context.getJobDetail().getKey().getGroup());
		jobRunHist.setFireTime(new java.util.Date());
		jobRunHist.setPreviousFireTime(trigger.getPreviousFireTime());
		jobRunHist.setNextFireTime(trigger.getNextFireTime());
		jobRunHist.setRefireCount(new Integer(context.getRefireCount()));
		if (jobException != null) {
			jobRunHist.setExceptionFlag(Boolean.TRUE);
			jobRunHist.setResult(jobException.getMessage());
			StringWriter strWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(new BufferedWriter(strWriter));
			jobException.printStackTrace(writer);
			writer.flush();
			strWriter.flush();
			String exceptionStack = strWriter.getBuffer().toString();
			jobRunHist.setExceptionStack(exceptionStack);
		} else {
			jobRunHist.setExceptionFlag(Boolean.FALSE);
			if (context.getResult() == null) {
				jobRunHist.setResult("SUCCESS");
			} else {
				String result = String.valueOf(context.getResult());
				jobRunHist.setResult(result);
			}
		}
		jobRunHistService.saveWithAsyncAndNewTransition(jobRunHist);
	}

}