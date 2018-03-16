package com.etrade.biz.purchase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.purchase.dao.PurchaseOrderDetailDao;
import com.etrade.biz.purchase.entity.PurchaseOrderDetail;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class PurchaseOrderDetailService extends BaseService<PurchaseOrderDetail, Long> {

    @Autowired
    private PurchaseOrderDetailDao purchaseOrderDetailDao;

    @Override
    protected BaseDao<PurchaseOrderDetail, Long> getEntityDao() {
        return purchaseOrderDetailDao;
    }
}
