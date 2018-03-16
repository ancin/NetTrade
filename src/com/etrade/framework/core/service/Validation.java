package com.etrade.framework.core.service;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.etrade.framework.core.exception.ValidationException;

/**
 * ����ҵ���߼�У��ġ����ԡ����ƣ��볣���Assert�������������׳� @see ValidationException
 * �����쳣������г����logger.error��¼��һ��ֻ��ǰ����ʾ��ʾ�û�
 */
public class Validation {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ValidationException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    public static void notEmpty(Collection collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ValidationException(message);
        }
    }

    public static void notDemoMode() {
        if (PropertiesConfigService.isDemoMode()) {
            throw new ValidationException("��ʾģʽ������������");
        }
    }
}
