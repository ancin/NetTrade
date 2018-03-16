package com.etrade.framework.core.ctx;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.etrade.framework.core.sys.entity.ConfigProperty;
import com.etrade.framework.core.sys.service.ConfigPropertyService;

/**
 * �������ݿ���ض�̬���ò���
 * �����չ���Լ��أ�Spring���˴�.properties������������
 * �������ݿ��������ͬ������������ȡ���ݿ������ֵ���������ļ��е�ֵ
 * Ϊ�˱�����������ݿ����õ���ϵͳ������Լ����cfg��ͷ��ʶ�Ĳ�����ʾ���Ա����ݿ������д��������򲻻Ḳ���ļ����������ֵ
 */
@Component
public class DynamicConfigService {

    private final Logger logger = LoggerFactory.getLogger(DynamicConfigService.class);

    @Value("${cfg.signup.disabled:false}")
    private String signupDisabled;

    @Value("${cfg.system.title:ETRADE}")
    private String systemTitle;

    @Value("${cfg.file.upload.dir:}")
    private String fileUploadDir;

    @Autowired(required = false)
    private ExtPropertyPlaceholderConfigurer extPropertyPlaceholderConfigurer;

    @Autowired
    private ConfigPropertyService configPropertyService;

    /**
     * ����key��ȡ��Ӧ��̬����ֵ
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * ����key��ȡ��Ӧ��̬����ֵ�����û���򷵻�defaultValue
     */
    public String getString(String key, String defaultValue) {
        String val = null;
        //���ȴ����ݿ�ȡֵ
        ConfigProperty cfg = configPropertyService.findByPropKey(key);
        if (cfg != null) {
            val = cfg.getSimpleValue();
        }
        //δȡ���������Spring�����ļ�����ȡ

        if (val == null) {
            if (extPropertyPlaceholderConfigurer != null) {
                val = extPropertyPlaceholderConfigurer.getProperty(key);
            } else {
                logger.warn("��ǰ������ExtPropertyPlaceholderConfigurer��չģʽ���壬����޷����ػ�ȡSpring��������");
            }
        }
        if (val == null) {
            return defaultValue;
        } else {
            return val.trim();
        }
    }

    public String getSystemTitle() {
        return getString("cfg.system.title", systemTitle);
    }

    public boolean isSignupDisabled() {
        return BooleanUtils.toBoolean(getString("cfg.signup.disabled", signupDisabled));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return BooleanUtils.toBoolean(getString(key, String.valueOf(defaultValue)));
    }

    private static String staticFileUploadDir;

    /**
     * ��ȡ�ļ��ϴ���Ŀ¼������ȡcfg.file.upload.dir����ֵ�����û�ж�����ȡ��ǰ�û���Ŀ¼${user.home}/attachments
     * @return
     */
    public String getFileUploadRootDir() {
        if (staticFileUploadDir == null) {
            staticFileUploadDir = fileUploadDir;
            if (StringUtils.isBlank(staticFileUploadDir)) {
                staticFileUploadDir = System.getProperty("user.home") + File.separator + "attachments";
            }
            if (staticFileUploadDir.endsWith(File.separator)) {
                staticFileUploadDir = staticFileUploadDir.substring(0, staticFileUploadDir.length() - 2);
            }
            logger.info("Setup file upload root dir:  {}", staticFileUploadDir);
        }
        return staticFileUploadDir;
    }
}