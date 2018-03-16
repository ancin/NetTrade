/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.audit.SaveUpdateAuditListener;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.entity.def.DefaultAuditable;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.etrade.framework.core.web.rest.Jackson2LibHandler;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonFilter(Jackson2LibHandler.DEFAULT_JSON_FILTER_NAME)
@JsonInclude(Include.NON_EMPTY)
@EntityListeners({ SaveUpdateAuditListener.class })
@MappedSuperclass
@AuditOverrides({ @AuditOverride(forClass = BaseEntity.class) })
public abstract class BaseEntity<ID extends Serializable> extends PersistableEntity<ID> implements
        DefaultAuditable<String, ID> {

    /**  */
    private static final long serialVersionUID = 1L;

    /** �ֹ����汾,��ʼ����Ϊ0 */
    private int version = 0;

    @MetaData(value = "���ݷ��ʿ��ƴ���", tooltips = "���ڷֻ��������ݷ��ʿ��ƴ���")
    protected String aclCode;

    /** ���ݷ��ʿ������� */
    protected String aclType;

    protected String createdBy;

    protected Date createdDate;

    protected String lastModifiedBy;

    protected Date lastModifiedDate;

    public abstract void setId(final ID id);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getCreatedBy()
     */
    @Override
    @JsonProperty
    @Column(updatable = false, name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
     */
    @Override
    @SkipParamBind
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    @Column(updatable = false, name = "created_dt")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    @JsonProperty
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    @SkipParamBind
    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
     */
    @Override
    @JsonIgnore
    @Column(name = "updated_by")
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    @SkipParamBind
    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_dt")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    @Override
    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Column(length = 20, nullable = true)
    public String getAclCode() {
        return aclCode;
    }

    @SkipParamBind
    public void setAclCode(String aclCode) {
        this.aclCode = aclCode;
    }

    public String getAclType() {
        return aclType;
    }

    @SkipParamBind
    public void setAclType(String aclType) {
        this.aclType = aclType;
    }

    @Version
    @Column(nullable = true)
    @JsonProperty
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void resetCommonProperties() {
        setId(null);
        version = 0;
        lastModifiedBy = null;
        lastModifiedDate = null;
        createdBy = null;
        createdDate = null;
        aclCode = null;
        aclType = null;
        addExtraAttribute(PersistableEntity.EXTRA_ATTRIBUTE_DIRTY_ROW, true);
    }

    private static final String[] PROPERTY_LIST = new String[] { "id", "version", "lastModifiedBy", "lastModifiedDate",
            "createdBy", "createdDate", "aclCode", "aclType" };

    public String[] retriveCommonProperties() {
        return PROPERTY_LIST;
    }

    @Override
    @Transient
    @JsonProperty
    public String getDisplay() {
        return "[" + getId() + "]" + this.getClass().getSimpleName();
    }
}
