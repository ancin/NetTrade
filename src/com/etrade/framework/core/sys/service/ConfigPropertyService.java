package com.etrade.framework.core.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.ConfigPropertyDao;
import com.etrade.framework.core.sys.entity.ConfigProperty;

@Service
@Transactional
public class ConfigPropertyService extends BaseService<ConfigProperty,String>{
    
    @Autowired
    private ConfigPropertyDao configPropertyDao;

    @Override
    protected BaseDao<ConfigProperty, String> getEntityDao() {
        return configPropertyDao;
    }
    
    public ConfigProperty findByPropKey(String propKey){
        return configPropertyDao.findByPropKey(propKey);
    }
}
