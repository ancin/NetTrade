package com.etrade.framework.core.exception;

import org.springframework.core.NestedRuntimeException;

public abstract class BaseRuntimeException extends NestedRuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public BaseRuntimeException(String msg) {
        super(msg);
    }
    
    public BaseRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
