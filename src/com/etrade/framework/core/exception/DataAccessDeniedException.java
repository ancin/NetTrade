package com.etrade.framework.core.exception;

/**
 * ���ݷ���Ȩ�޲���
 */
@SuppressWarnings("serial")
public class DataAccessDeniedException extends BaseRuntimeException{


    public DataAccessDeniedException() {
        super("��Ȩ���ݷ���");
    }
    
    public DataAccessDeniedException(String msg) {
        super(msg);
    }

    public DataAccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
