package com.etrade.biz.sale.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.md.dao.CommodityDao;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.sale.dao.SaleDeliveryDao;
import com.etrade.biz.sale.entity.SaleDelivery;
import com.etrade.biz.sale.entity.SaleDeliveryDetail;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.service.Validation;

@Service
@Transactional
public class SaleDeliveryService extends BaseService<SaleDelivery, Long> {

    @Autowired
    private SaleDeliveryDao saleDeliveryDao;

    @Autowired
    private CommodityDao commodityDao;

    @Override
    protected BaseDao<SaleDelivery, Long> getEntityDao() {
        return saleDeliveryDao;
    }

    @Override
    public SaleDelivery save(SaleDelivery entity) {
        BigDecimal commodityCostAmount = BigDecimal.ZERO;
        BigDecimal originalAmount = BigDecimal.ZERO;
        BigDecimal commodityAmount = BigDecimal.ZERO;
        for (SaleDeliveryDetail sdd : entity.getSaleDeliveryDetails()) {
            sdd.setSaleDelivery(entity);
            Commodity commodity = commodityDao.findOne(sdd.getCommodity().getId());
            //先按照最简单的商品上面维护成本价，后面完善为分库存地成本价维护
            Validation.notNull(commodity.getCostPrice(), "请首先维护商品[" + commodity.getSku() + "]成本价");
            sdd.setCostPrice(commodity.getCostPrice());
            sdd.setCostAmount(sdd.getCostPrice().multiply(sdd.getQuantity()));

            commodityCostAmount = commodityCostAmount.add(sdd.getCostAmount());
            originalAmount = originalAmount.add(sdd.getOriginalAmount());
            commodityAmount = commodityAmount.add(sdd.getAmount());
        }

        entity.setCommodityCostAmount(commodityCostAmount);
        entity.setOriginalAmount(originalAmount);
        entity.setCommodityAmount(commodityAmount);
        return super.save(entity);
    }
}

