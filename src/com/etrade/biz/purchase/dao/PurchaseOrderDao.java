package com.etrade.biz.purchase.dao;

import org.springframework.stereotype.Repository;

import com.etrade.biz.purchase.entity.PurchaseOrder;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface PurchaseOrderDao extends BaseDao<PurchaseOrder, Long> {


}