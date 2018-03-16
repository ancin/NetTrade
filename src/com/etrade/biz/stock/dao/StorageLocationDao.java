package com.etrade.biz.stock.dao;

import org.springframework.stereotype.Repository;

import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface StorageLocationDao extends BaseDao<StorageLocation, String> {

}