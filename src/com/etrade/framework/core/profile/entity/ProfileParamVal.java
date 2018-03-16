package com.etrade.framework.core.profile.entity;

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
@Table(name = "TBL_PROFILE_PARAM_DEF", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "个性化配置参数数据")
public class ProfileParamVal extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    private User user;

    private ProfileParamDef profileParamDef;

    @Override
    @Transient
    public String getDisplay() {
        return user.getDisplay() + " " + profileParamDef.getDisplay();
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "PROFILE_PARAM_DEF_ID")
    @JsonIgnore
    public ProfileParamDef getProfileParamDef() {
        return profileParamDef;
    }

    public void setProfileParamDef(ProfileParamDef profileParamDef) {
        this.profileParamDef = profileParamDef;
    }

}
