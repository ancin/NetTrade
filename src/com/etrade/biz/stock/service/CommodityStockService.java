package com.etrade.biz.stock.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.stock.dao.CommodityStockDao;
import com.etrade.biz.stock.entity.CommodityStock;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class CommodityStockService extends BaseService<CommodityStock, Long> {
    @Autowired
    private CommodityStockDao commodityStockDao;

    @Override
    protected BaseDao<CommodityStock, Long> getEntityDao() {
        return commodityStockDao;
    }

    public CommodityStock findBy(Commodity commodity, StorageLocation storageLocation) {
        return findBy(commodity, storageLocation, null);
    }

    public CommodityStock findBy(Commodity commodity, StorageLocation storageLocation, String batchNo) {
        CommodityStock commodityStock = null;
        if (StringUtils.isNotBlank(batchNo)) {
            commodityStock = commodityStockDao.findByCommodityAndStorageLocationAndBatchNo(commodity, storageLocation,
                    batchNo);
        } else {
            commodityStock = commodityStockDao.findByCommodityAndStorageLocation(commodity, storageLocation);
        }
        return commodityStock;
    }

    @Override
    public CommodityStock save(CommodityStock entity) {
        throw new UnsupportedOperationException("不允许直接修改库存量数据，需使用库存移动接口级联计算更新库存数据");
    }
}
