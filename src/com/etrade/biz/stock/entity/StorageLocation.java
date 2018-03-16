package com.etrade.biz.stock.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

@MetaData(value = "¿â´æµØ")
@Entity
@Table(name = "biz_stock_storage_location")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorageLocation extends BaseUuidEntity {
    /**  */
    private static final long serialVersionUID = 1L;
    @MetaData(value = "±àÂë")
    private String            code;
    @MetaData(value = "Ãû³Æ")
    private String            title;
    @MetaData(value = "µØÖ·")
    private String            addr;

    @Column(length = 32, unique = true, nullable = false)
    @JsonProperty
    @Size(min = 3, max = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 128, nullable = false)
    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 512)
    @JsonProperty
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    @Transient
    @JsonProperty
    public String getDisplay() {
        return code + " " + title;
    }
}