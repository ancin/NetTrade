package com.etrade.framework.core.exception;

public class WebException extends BaseRuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public WebException(String msg) {
        super(msg);
    }

    public WebException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
