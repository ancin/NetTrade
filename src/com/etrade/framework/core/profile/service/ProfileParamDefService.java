package com.etrade.framework.core.profile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.profile.dao.ProfileParamDefDao;
import com.etrade.framework.core.profile.entity.ProfileParamDef;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class ProfileParamDefService extends BaseService<ProfileParamDef,String>{
    
    @Autowired
    private ProfileParamDefDao profileParamDefDao;

    @Override
    protected BaseDao<ProfileParamDef, String> getEntityDao() {
        return profileParamDefDao;
    }
}
