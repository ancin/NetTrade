package com.etrade.framework.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.def.DynamicParameterDef;

@Entity
@Table(name = "TBL_PROFILE_PARAM_DEF", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "个性化配置参数定义")
public class ProfileParamDef extends DynamicParameterDef {

    /**  */
    private static final long serialVersionUID = 1L;

    @Override
    @Transient
    public String getDisplay() {
        return getTitle();
    }

}
