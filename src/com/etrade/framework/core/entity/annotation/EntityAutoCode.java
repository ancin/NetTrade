package com.etrade.framework.core.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * ���ڴ��빤�����ɿ�ܴ���ı�ʶע��
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface EntityAutoCode {
    
    /**
     * ��ѯ�������ɱ�ʶ
     * @return
     */
    boolean search() default false;
    
    /**
     * �߼���ѯ�������ɱ�ʶ
     * @return
     */
    boolean searchAdvance() default false;
    
    /**
     * ����Grid�б�Ԫ�ز�����Ϊhidden=false
     * @return
     */
    boolean listShow() default true;
    
    /**
     * ����Grid�б�Ԫ�ز�����Ϊhidden=true
     * @return
     */
    boolean listHidden() default false;

    /**
     * ���ɱ༭����Ԫ�ر�ʶ
     * @return
     */
    boolean edit() default true;
    
    /**
     * Ԫ����ʾ˳��
     * @return
     */
    int order() default Integer.MAX_VALUE;
    
    /**
     * �����Ƿ����ڶ�̬��ʾ�Ա���ͼ
     * @return
     */
    boolean comparable() default true;
}
