package com.etrade.framework.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface HibEventListeners {
    /** The callback listener classes */
    @SuppressWarnings("rawtypes")
    Class[] value();
}
