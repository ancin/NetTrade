package com.etrade.framework.core.exception;

/**
 * /**
 * ҵ���߼�У���쳣�������쳣������г����logger.error��¼��һ��ֻ��ǰ����ʾ��ʾ�û�
 */
public class ValidationException extends BaseRuntimeException {

    /**  */
    private static final long serialVersionUID = 1L;

    public ValidationException(String msg) {
        super(msg);
    }
}
