/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.entity;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import com.etrade.framework.core.audit.SaveUpdateAuditListener;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ����ṩһ��������Native��ʽ��ʵ�������ο�
 * ����ɸ�����Ŀ����ѡ���������������������еȷ�ʽ��ֻ���޸���ط��Ͳ������ͺ���������ע�⼴��
 * �����Զ�����ȼ򵥶���MetaDataע�⼴�ɣ�����ϸ�ڵĿ������Ժ���ɲ鿴�������ע��˵��
 */
@JsonInclude(Include.NON_EMPTY)
@EntityListeners({ SaveUpdateAuditListener.class })
@MappedSuperclass
@AuditOverrides({ @AuditOverride(forClass = BaseNativeEntity.class) })
public abstract class BaseNativeEntity extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;
    private Long id;

    @Override
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "native")
    @JsonProperty
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }
}
