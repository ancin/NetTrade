package com.etrade.framework.core.exception;

/**
 * ���ݲ�����Ȩ
 */
public class DataOperationDeniedException extends BaseRuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public DataOperationDeniedException() {
        super("��Ч���ݲ���");
    }
    
    public DataOperationDeniedException(String msg) {
        super(msg);
    }

    public DataOperationDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
