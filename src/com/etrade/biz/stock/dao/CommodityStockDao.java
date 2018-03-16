package com.etrade.biz.stock.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.stock.entity.CommodityStock;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface CommodityStockDao extends BaseDao<CommodityStock, Long> {

    @Query("from CommodityStock where commodity=:commodity and storageLocation=:storageLocation and (batchNo is null or batchNo='')")
    CommodityStock findByCommodityAndStorageLocation(@Param("commodity") Commodity commodity,
            @Param("storageLocation") StorageLocation storageLocation);

    CommodityStock findByCommodityAndStorageLocationAndBatchNo(Commodity commodity, StorageLocation storageLocation,
            String batchNo);

    List<CommodityStock> findByCommodity(Commodity commodity);
}