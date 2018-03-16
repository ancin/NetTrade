package com.etrade.biz.purchase.dao;

import org.springframework.stereotype.Repository;

import com.etrade.biz.purchase.entity.PurchaseOrderDetail;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface PurchaseOrderDetailDao extends BaseDao<PurchaseOrderDetail, Long> {

}