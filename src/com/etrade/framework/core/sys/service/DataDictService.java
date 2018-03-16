package com.etrade.framework.core.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.DataDictDao;
import com.etrade.framework.core.sys.entity.DataDict;
import com.google.common.collect.Maps;

@Service
@Transactional
public class DataDictService extends BaseService<DataDict, String> {

    @Autowired
    private DataDictDao dataDictDao;

    @Autowired
    private MessageSource messageSource;

    @Override
    protected BaseDao<DataDict, String> getEntityDao() {
        return dataDictDao;
    }

    public List<DataDict> findAllCached() {
        return dataDictDao.findAllCached();
    }

    public List<DataDict> findChildrenByPrimaryKey(String primaryKey) {
        DataDict parent = dataDictDao.findByPrimaryKey(primaryKey);
        return dataDictDao.findChildrenByParentAndDisabled(parent, false);
    }

    public Map<String, String> findMapDataByPrimaryKey(String primaryKey) {
        Map<String, String> dataMap = Maps.newLinkedHashMap();
        List<DataDict> dataDicts = findChildrenByPrimaryKey(primaryKey);
        for (DataDict dataDict : dataDicts) {
            dataMap.put(dataDict.getPrimaryKey(), dataDict.getPrimaryValue());
        }
        return dataMap;
    }
}
