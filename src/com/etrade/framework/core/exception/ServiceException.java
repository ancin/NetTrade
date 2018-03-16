package com.etrade.framework.core.exception;

public class ServiceException extends BaseRuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
