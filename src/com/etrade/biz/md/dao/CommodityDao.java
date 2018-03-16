package com.etrade.biz.md.dao;

import org.springframework.stereotype.Repository;

import com.etrade.biz.md.entity.Commodity;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface CommodityDao extends BaseDao<Commodity, String> {

}