package com.etrade.biz.md.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.md.dao.CommodityDao;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class CommodityService extends BaseService<Commodity, String> {

    @Autowired
    private CommodityDao commodityDao;

    @Override
    protected BaseDao<Commodity, String> getEntityDao() {
        return commodityDao;
    }

    public Commodity findByBarcode(String barcode) {
        return this.findByProperty("barcode", barcode);
    }
}
