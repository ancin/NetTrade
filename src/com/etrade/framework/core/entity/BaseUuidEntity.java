/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.entity;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import com.etrade.framework.core.audit.SaveUpdateAuditListener;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ����ṩһ��������UUID��ʽ��ʵ�������ο�
 * ����ɸ�����Ŀ����ѡ���������������������еȷ�ʽ��ֻ���޸���ط��Ͳ������ͺ���������ע�⼴��
 * �����Զ�����ȼ򵥶���MetaDataע�⼴�ɣ�����ϸ�ڵĿ������Ժ���ɲ鿴�������ע��˵��
 */
@JsonInclude(Include.NON_EMPTY)
@EntityListeners({ SaveUpdateAuditListener.class })
@MappedSuperclass
@AuditOverrides({ @AuditOverride(forClass = BaseUuidEntity.class) })
public abstract class BaseUuidEntity extends BaseEntity<String> {

    /**  */
    private static final long serialVersionUID = 1L;
    private String id;

    @Override
    @Id
    @Column(length = 40)
    @GeneratedValue(generator = "hibernate-uuid")
    //HHH000409: Using org.hibernate.id.UUIDHexGenerator which does not generate IETF RFC 4122 compliant UUID values; consider using org.hibernate.id.UUIDGenerator instead 
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @JsonProperty
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        //�ݴ���id�Կ��ַ��ύ����ʱ�޸�Ϊnull����hibernate������Ч�޸�
        if (StringUtils.isBlank(id)) {
            this.id = null;
        } else {
            this.id = id;
        }
    }
}
