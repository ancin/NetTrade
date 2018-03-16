package com.etrade.framework.core.web.listener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.dao.jpa.ExtPersistenceUnitPostProcessor;
import com.google.common.collect.Maps;

/**
 * Spring�������ء�֮�󡱵�ServletContextListener
 */
public class ApplicationContextPostListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextPostListener.class);

    @SuppressWarnings("unchecked")
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.debug("Invoke ApplicationContextPostListener contextInitialized");
        try {
            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event
                    .getServletContext());

            SpringContextHolder.setApplicationContext(applicationContext);

            ServletContext sc = event.getServletContext();
            String appName = sc.getServletContextName();
            logger.info("[{}] init context ...", appName);

            Map<String, Map<? extends Serializable, String>> scEnumsMap = Maps.newHashMap();
            //�洢ö�����ƺͶ�ӦClassӳ���Map
            Map<String, Class<?>> enumShortNameClassMapping = Maps.newHashMap();

            ExtPersistenceUnitPostProcessor persistenceUnitPostProcessor = (ExtPersistenceUnitPostProcessor) applicationContext
                    .getBean(PersistenceUnitPostProcessor.class);

            MutablePersistenceUnitInfo pui = persistenceUnitPostProcessor.getMutablePersistenceUnitInfo();

            //ѭ������Entity�����е�Enum���壬ת���ɶ�Ӧ��Map�ṹ���ݴ���ServletContext�����У�����Web���ǩֱ�ӷ����ȡ
            for (Class<?> entityClass : convertClassNamesToClasses(pui.getManagedClassNames())) {
                logger.trace("Post processing entity: {}", entityClass);
                Field[] fields = entityClass.getDeclaredFields();
                for (Field field : fields) {
                    @SuppressWarnings("rawtypes")
                    Class fieldClass = field.getType();
                    if (fieldClass.isEnum()) {
                        String simpleName = fieldClass.getSimpleName();
                        Class<?> existClass = enumShortNameClassMapping.get(simpleName);
                        if (existClass == null) {
                            //logger.info(" - Put Enum short name mapping: {}={}", simpleName, fieldClass);
                            enumShortNameClassMapping.put(simpleName, fieldClass);
                        } else {
                            if (!existClass.equals(fieldClass)) {
                                throw new IllegalStateException("Duplicate simple name: " + simpleName + ", class1="
                                        + existClass + ", class2=" + fieldClass);
                            } else {
                                continue;
                            }
                        }

                        @SuppressWarnings("rawtypes")
                        Map<java.lang.Enum, String> enumDataMap = Maps.newLinkedHashMap();
                        for (Field enumfield : fieldClass.getFields()) {
                            MetaData entityComment = enumfield.getAnnotation(MetaData.class);
                            String value = enumfield.getName();
                            if (entityComment != null) {
                                value = entityComment.value();
                            }
                            enumDataMap.put(Enum.valueOf(fieldClass, enumfield.getName()), value);
                        }
                        String attrName = StringUtils.uncapitalize(fieldClass.getSimpleName());
                        scEnumsMap.put(attrName, enumDataMap);
                    }
                }
            }

            //����Ĭ�ϵ�boolean��������ת����ʾ
            Map<Boolean, String> booleanLabelMap = Maps.newLinkedHashMap();
            booleanLabelMap.put(Boolean.TRUE, "��");
            booleanLabelMap.put(Boolean.FALSE, "��");
            scEnumsMap.put("booleanLabel", booleanLabelMap);

            if (logger.isInfoEnabled()) {
                logger.info("Put enums data to ServletContext: ");
                for (Map.Entry<String, Map<? extends Serializable, String>> me : scEnumsMap.entrySet()) {
                    logger.info(" -  {} = {}", me.getKey(), me.getValue());
                }
            }
            sc.setAttribute("enums", scEnumsMap);
        } catch (Exception e) {
            logger.error("error detail:", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.debug("Invoke ApplicationContextPostListener contextDestroyed");
    }

    /**
     * ����ʵ��Apache Commons Lang��ClassUtils��Ӧ�ӿ�ʵ��
     * ��Ҫ��Ϊ�˽����Ӧ�õ�jar�ļ���tomcat\lib����ģʽ��������ػ��Ʒ�������
     * @param classNames
     * @return
     */
    private List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
        if (classNames == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }
}
