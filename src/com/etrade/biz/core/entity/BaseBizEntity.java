package com.etrade.biz.core.entity;

import javax.persistence.MappedSuperclass;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import com.etrade.framework.core.entity.BaseNativeEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@MappedSuperclass
@AuditOverrides({ @AuditOverride(forClass = BaseBizEntity.class) })
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public abstract class BaseBizEntity extends BaseNativeEntity {

    /**  */
    private static final long serialVersionUID = 1L;

}
