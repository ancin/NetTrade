package com.etrade.framework.core.sys.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_SYS_CFG_PROP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "��������")
public class ConfigProperty extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    @MetaData(value = "����")
    @EntityAutoCode(order = 10)
    private String propKey;

    @MetaData(value = "����")
    @EntityAutoCode(order = 20)
    private String propName;

    @MetaData(value = "������ֵ")
    @EntityAutoCode(order = 30)
    private String simpleValue;

    @MetaData(value = "HTML����ֵ")
    @EntityAutoCode(order = 40)
    private String htmlValue;

    @MetaData(value = "���������÷�˵��")
    @EntityAutoCode(order = 50)
    private String propDescn;

    @Override
    @Transient
    public String getDisplay() {
        return propKey;
    }

    @Column(length = 64, unique = true)
    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    @Column(length = 256)
    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    @JsonIgnore
    @Column(length = 2000)
    public String getPropDescn() {
        return propDescn;
    }

    public void setPropDescn(String propDescn) {
        this.propDescn = propDescn;
    }

    @Column(length = 256)
    public String getSimpleValue() {
        return simpleValue;
    }

    public void setSimpleValue(String simpleValue) {
        this.simpleValue = simpleValue;
    }

    @Lob
    @JsonIgnore
    public String getHtmlValue() {
        return htmlValue;
    }

    public void setHtmlValue(String htmlValue) {
        this.htmlValue = htmlValue;
    }
}
