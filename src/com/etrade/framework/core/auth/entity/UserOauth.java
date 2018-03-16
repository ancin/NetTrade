package com.etrade.framework.core.auth.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;

@Entity
@Table(name = "tbl_AUTH_USER_OAUTH")
@MetaData(value = "�û���OAUTH��֤")
public class UserOauth extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;
    private User user;
    private String username;
    private String providerUid;
    private String providerType;
    private Date bindTime;

    @Column(length = 128, nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(length = 128, nullable = false)
    public String getProviderUid() {
        return providerUid;
    }

    public void setProviderUid(String providerUid) {
        this.providerUid = providerUid;
    }

    @Column(length = 128, nullable = false)
    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    @Override
    @Transient
    public String getDisplay() {
        // TODO Auto-generated method stub
        return null;
    }

}
