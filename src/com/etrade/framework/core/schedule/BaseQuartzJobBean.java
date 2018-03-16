package com.etrade.framework.core.schedule;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.etrade.framework.core.ctx.FreemarkerService;
import com.etrade.framework.core.exception.ServiceException;
import com.etrade.framework.core.schedule.entity.JobBeanCfg;
import com.etrade.framework.core.schedule.service.JobBeanCfgService;

/**
 * �Զ���Quartz Jobҵ�����Ļ��ඨ��
 * ҵ��Job�̳д˳�����࣬���Spring ApplicationContext�������Ӷ����Ի�ȡSpring������Bean����
 * ͬʱʵ��QuartzJobBeanԼ���ӿڣ���д��ʱ�����߼�
 */
public abstract class BaseQuartzJobBean extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(BaseQuartzJobBean.class);

	protected ApplicationContext applicationContext;

	    /**
     * ��SchedulerFactoryBeanע���applicationContext.
     */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("Set applicationContext for QuartzJobBean");
		this.applicationContext = applicationContext;
	}

	protected <X> X getSpringBean(Class<X> clazz) {
		return this.applicationContext.getBean(clazz);
	}

	protected JdbcTemplate getJdbcTemplate() {
		return getSpringBean(JdbcTemplate.class);
	}

	    /**
     * ����Freemarker��װ�����������ı�
     * @param context
     * @param dataMap
     * @return
     */
	protected String buildJobResultByTemplate(JobExecutionContext context, Map<String, Object> dataMap) {
		JobBeanCfgService jobBeanCfgService = getSpringBean(JobBeanCfgService.class);
		FreemarkerService freemarkerService = getSpringBean(FreemarkerService.class);

		JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(context.getJobDetail().getJobClass().getName());
		if (jobBeanCfg != null) {
			String resultTemplate = jobBeanCfg.getResultTemplate();
			if (StringUtils.isNotBlank(resultTemplate)) {
				String result = freemarkerService.processTemplate(jobBeanCfg.getJobClass(), jobBeanCfg.getVersion(),
						resultTemplate, dataMap);
				return result;
			}
		}
		return "UNDEFINED";
	}

	@Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("Invoking executeInternalBiz for {}", this.getClass());
			executeInternalBiz(context);
			logger.debug("Job execution result: {}", context.getResult());
		} catch (Exception e) {
			logger.error("Quartz job execution error", e);
			throw new ServiceException(e.getMessage(), e);
		}
	}

	protected abstract void executeInternalBiz(JobExecutionContext context);
}
