package com.etrade.biz.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.stock.dao.StorageLocationDao;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class StorageLocationService extends BaseService<StorageLocation,String>{
    
    @Autowired
    private StorageLocationDao storageLocationDao;

    @Override
    protected BaseDao<StorageLocation, String> getEntityDao() {
        return storageLocationDao;
    }
}
