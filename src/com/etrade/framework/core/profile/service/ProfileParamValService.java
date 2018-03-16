package com.etrade.framework.core.profile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.profile.dao.ProfileParamValDao;
import com.etrade.framework.core.profile.entity.ProfileParamVal;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class ProfileParamValService extends BaseService<ProfileParamVal,String>{
    
    @Autowired
    private ProfileParamValDao profileParamValDao;

    @Override
    protected BaseDao<ProfileParamVal, String> getEntityDao() {
        return profileParamValDao;
    }
}
