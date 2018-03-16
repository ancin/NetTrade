package com.etrade.framework.core.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.service.PropertiesConfigService;

/**
 * Spring�������ء�֮ǰ����ServletContextListener
 */
public class ApplicationContextPreListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextPreListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //���õ�ǰWEB_ROOT��Ŀ¼�����������Ա��ڵ�����Service���л���ȡ��Ӧ�ø�Ŀ¼��ȡWEB-INF�������Դ
        PropertiesConfigService.setWebRootRealPath(event.getServletContext().getRealPath("/"));
        
        logger.debug("Invoke ApplicationContextPreListener contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Invoke ApplicationContextPreListener contextDestroyed");
    }

}
