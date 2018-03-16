package com.etrade.framework.core.service;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.web.listener.ApplicationContextPreListener;

@Component
public class PropertiesConfigService {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigService.class);

    @MetaData(value = "����ģʽ", comments = "�����ɵ�Ȩ�޿��ƣ��������־��Ϣ�����application.properties���ò�������")
    private static boolean devMode = false;

    @MetaData(value = "��ʾģʽ", comments = "����ʾ����������������Ա��ⲻ��Ҫ�����������޸ĵ���ϵͳ����")
    private static boolean demoMode = false;

    /**
     * @see ApplicationContextPreListener#contextInitialized
     */
    @MetaData(value = "WebӦ�ò���ĸ�Ŀ¼", comments = "���ڻ�ȡWEB-INFĿ¼����Դ��")
    private static String webRootRealPath;

    public static boolean isDemoMode() {
        return demoMode;
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static String getWebRootRealPath() {
        Assert.notNull(webRootRealPath, "WEB_ROOT real path undefined");
        return webRootRealPath;
    }

    @Value("${demo.mode:false}")
    public void setDemoMode(String demoMode) {
        PropertiesConfigService.demoMode = BooleanUtils.toBoolean(demoMode);
        logger.info("System runnging at demo.mode={}", PropertiesConfigService.demoMode);
    }

    @Value("${dev.mode:true}")
    public void setDevMode(String devMode) {
        PropertiesConfigService.devMode = BooleanUtils.toBoolean(devMode);
        logger.info("System runnging at dev.mode={}", PropertiesConfigService.devMode);
    }

    @Value("${web.root.real.path:''}")
    public static void setWebRootRealPath(String in) {
        if (webRootRealPath == null) {
            webRootRealPath = in;
        }
        if (StringUtils.isNotBlank(webRootRealPath)) {
            logger.info("System runnging at web.root.real.path={}", webRootRealPath);
        }
    }
}
