package com.etrade.framework.core.web.captcha;

public class BadCaptchaException extends RuntimeException{

    /**  */
    private static final long serialVersionUID = 1L;

    public BadCaptchaException(String message, Throwable e) {
        super(message, e);
    }

    public BadCaptchaException(String message) {
        super(message);
    }

}
