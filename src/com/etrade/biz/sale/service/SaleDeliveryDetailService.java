package com.etrade.biz.sale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.sale.dao.SaleDeliveryDetailDao;
import com.etrade.biz.sale.entity.SaleDeliveryDetail;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class SaleDeliveryDetailService extends BaseService<SaleDeliveryDetail,Long>{
    
    @Autowired
    private SaleDeliveryDetailDao saleDeliveryDetailDao;

    @Override
    protected BaseDao<SaleDeliveryDetail, Long> getEntityDao() {
        return saleDeliveryDetailDao;
    }
}
