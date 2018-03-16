package com.etrade.framework.core.sys.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Maps;

@MetaData(value = "��������")
public class UtilController extends SimpleController {

    protected final static Logger logger = LoggerFactory.getLogger(UtilController.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private CacheManager cacheManager;

    @MetaData(value = "��������")
    public HttpHeaders mgmt() {
        return buildDefaultHttpHeaders("mgmt");
    }

    public List<String> getCacheNames() {
        List<String> datas = new ArrayList<String>();
        for (String cacheName : cacheManager.getCacheNames()) {
            datas.add(cacheName);
        }
        Collections.sort(datas);
        return datas;
    }

    @MetaData(value = "ˢ�����ݻ���")
    public HttpHeaders dataEvictCache() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String[] cacheNames = request.getParameterValues("cacheNames");

        SessionFactory sessionFactory = ((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();

        if (cacheNames != null && cacheNames.length > 0 && cacheNames[0].trim().length() > 0) {
            if (logger.isInfoEnabled()) {
                logger.info("Evicting Hibernate&Spring Cache, Scope: {}", StringUtils.join(cacheNames, ","));
            }
            for (String cacheName : cacheNames) {
                if (StringUtils.isNotBlank(cacheName)) {
                    sessionFactory.getCache().evictQueryRegion(cacheName);
                    sessionFactory.getCache().evictEntityRegion(cacheName);
                    cacheManager.getCache(cacheName).clear();
                }
            }
        } else {
            logger.info("Evicting Hibernate&Spring Cache, Scope: All");
            sessionFactory.getCache().evictEntityRegions();
            sessionFactory.getCache().evictCollectionRegions();
            sessionFactory.getCache().evictQueryRegions();
            for (String cacheName : cacheManager.getCacheNames()) {
                cacheManager.getCache(cacheName).clear();
            }
        }
        model = OperationResult.buildSuccessResult("���ݻ���ˢ�²����ɹ�");
        return new DefaultHttpHeaders().disableCaching();
    }

    public Map<String, String> getLoggerList() {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggers = loggerContext.getLoggerList();
        for (Logger logger : loggers) {
            dataMap.put(logger.getName(), logger.getName());
        }
        return dataMap;
    }

    @MetaData(value = "��־�������")
    public HttpHeaders loggerLevelUpdate() {
        Validation.notDemoMode();
        HttpServletRequest request = ServletActionContext.getRequest();
        String loggerName = request.getParameter("loggerName");
        String loggerLevel = request.getParameter("loggerLevel");
        if (StringUtils.isBlank(loggerName)) {
            loggerName = Logger.ROOT_LOGGER_NAME;
        }
        Logger logger = LoggerFactory.getLogger(loggerName);
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        if (StringUtils.isNotBlank(loggerLevel)) {
            logbackLogger.setLevel(Level.toLevel(loggerLevel));
        }
        model = OperationResult.buildSuccessResult("��־����ˢ�²����ɹ�");
        return new DefaultHttpHeaders().disableCaching();
    }

    @MetaData(value = "��������")
    public HttpHeaders dev() {
        return buildDefaultHttpHeaders("dev");
    }

    private static Server h2Server;

    @MetaData(value = "����H2 Server")
    public HttpHeaders startH2() throws Exception {
        Validation.notDemoMode();
        Map<String, String> datas = Maps.newHashMap();
        EmbeddedDatabaseFactoryBean db = SpringContextHolder.getBean(EmbeddedDatabaseFactoryBean.class);
        String databaseName = (String) FieldUtils.readField(db, "databaseName", true);
        databaseName = "jdbc:h2:file:" + databaseName;
        logger.info("H2 JDBC URL: {}", databaseName);
        if (h2Server == null) {
            h2Server = Server.createWebServer();
        }
        try {
            logger.info("Starting H2 Server...");
            h2Server.start();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        datas.put("h2LoginUrl", "http://localhost:" + h2Server.getPort() + "/login.jsp");
        datas.put("h2JdbcUrl", databaseName);
        setModel(OperationResult.buildSuccessResult("H2 Server�ѳɹ�����", datas));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "�ر�H2 Server")
    public HttpHeaders stopH2() throws Exception {
        Validation.notDemoMode();
        if (h2Server != null) {
            logger.info("Stopping H2 Server...");
            h2Server.stop();
        }
        setModel(OperationResult.buildSuccessResult("H2 Server�ѹر�"));
        return buildDefaultHttpHeaders();
    }
}
