package com.etrade.framework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.etrade.framework.core.common.PersistableController;

/**
 * ����ע��������Ե�Ԫ���ݣ���ЩԪ���ݿ����ڴ������ɻ�����ʱ��̬��������
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE })
public @interface MetaData {

    /**
     * ��Ҫע��˵����һ���Ӧ����Label������ʾ
     */
    String value();

    /**
     * ��ʾ��Ϣ��һ���Ӧ�������ʾ˵����֧����HTML��ʽ
     */
    String tooltips() default "";

    /**
     * ע��˵�����������������ڲ��÷�˵����һ�㲻����ǰ��UI��ʾ
     */
    String comments() default "";

    /**
     * ��ʶ�����Ƿ�����ڰ汾�Ƚ��б�
     * @see PersistableController#getRevisionFields()
     */
    boolean comparable() default true;
}
