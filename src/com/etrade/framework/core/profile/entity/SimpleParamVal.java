package com.etrade.framework.core.profile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "TBL_PROFILE_SIMPLE_PARAM", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_ID", "CODE" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "个性化配置参数数据")
public class SimpleParamVal extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    private User user;

    private String code;

    private String value;

    @Override
    @Transient
    public String getDisplay() {
        return user.getDisplay() + " " + code + " " + value;
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(length = 200, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
