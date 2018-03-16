package com.etrade.framework.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.etrade.framework.core.web.interceptor.ExtParametersInterceptor;

/**
 * @see ExtParametersInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SkipParamBind {

}
