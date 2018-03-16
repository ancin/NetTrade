package com.etrade.framework.core.exception;

public class DuplicateTokenException extends BaseRuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public DuplicateTokenException(String msg) {
        super(msg);
    }

    public DuplicateTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
