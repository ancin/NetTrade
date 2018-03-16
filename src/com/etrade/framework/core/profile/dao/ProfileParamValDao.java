package com.etrade.framework.core.profile.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.profile.entity.ProfileParamVal;

@Repository
public interface ProfileParamValDao extends BaseDao<ProfileParamVal, String> {

}