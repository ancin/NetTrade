package com.etrade.framework.core.web.listener;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

/**
 * ����չSpring��׼��ContextLoaderListener���Ա���ݹ���jar����ģʽ
 */
public class ApplicationContextLoaderListener extends ContextLoaderListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (logger.isInfoEnabled()) {
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
            logger.info("Using ClassLoader[{}]: {}", originalLoader.hashCode(), originalLoader);
        }
        super.contextInitialized(event);
    }

}
