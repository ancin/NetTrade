package com.etrade.biz.stock.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.md.dao.CommodityDao;
import com.etrade.biz.stock.dao.CommodityStockDao;
import com.etrade.biz.stock.dao.StockInOutDao;
import com.etrade.biz.stock.entity.CommodityStock;
import com.etrade.biz.stock.entity.StockInOut;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class StockInOutService extends BaseService<StockInOut, Long> {

    @Autowired
    private StockInOutDao stockInOutDao;
    @Autowired
    private CommodityStockDao commodityStockDao;
    @Autowired
    private CommodityDao commodityDao;

    @Override
    protected BaseDao<StockInOut, Long> getEntityDao() {
        return stockInOutDao;
    }

    public List<StockInOut> findByVoucherAndVoucherType(String voucher, VoucherTypeEnum voucherType) {

        return stockInOutDao.findByVoucherAndVoucherType(voucher, voucherType);
    }

    public void saveCascade(StockInOut entity) {
        //更新商品库存量
        CommodityStock commodityStock = entity.getCommodityStock();
        entity.setOriginalPurchasingQuantity(commodityStock.getPurchasingTotalQuantity());
        entity.setOriginalSalingQuantity(commodityStock.getSalingTotalQuantity());
        entity.setOriginalQuantity(commodityStock.getCurStockQuantity());
        if (entity.getDiffSalingQuantity().compareTo(BigDecimal.ZERO) != 0) {
            if (commodityStock.getSalingTotalQuantity() == null) {
                commodityStock.setSalingTotalQuantity(entity.getDiffSalingQuantity());
            } else {
                commodityStock.setSalingTotalQuantity(commodityStock.getSalingTotalQuantity().add(
                        entity.getDiffSalingQuantity()));
            }
        }
        entity.setSalingQuantity(commodityStock.getSalingTotalQuantity());

        if (entity.getDiffPurchasingQuantity().compareTo(BigDecimal.ZERO) != 0) {
            if (commodityStock.getPurchasingTotalQuantity() == null) {
                commodityStock.setPurchasingTotalQuantity(entity.getDiffPurchasingQuantity());
            } else {
                commodityStock.setPurchasingTotalQuantity(commodityStock.getPurchasingTotalQuantity().add(
                        entity.getDiffPurchasingQuantity()));
            }
        }
        entity.setPurchasingQuantity(commodityStock.getPurchasingTotalQuantity());

        if (entity.getDiffQuantity().compareTo(BigDecimal.ZERO) != 0) {
            if (commodityStock.getCurStockQuantity() == null) {
                commodityStock.setCurStockQuantity(entity.getDiffQuantity());
            } else {
                commodityStock.setCurStockQuantity(commodityStock.getCurStockQuantity().add(entity.getDiffQuantity()));
            }
        }
        entity.setQuantity(commodityStock.getCurStockQuantity());

        commodityStock.setCurStockAmount(commodityStock.getCurStockQuantity().multiply(commodityStock.getCostPrice()));

        commodityStockDao.save(commodityStock);
        stockInOutDao.save(entity);
    }

    public void redword(String voucher, VoucherTypeEnum voucherType, String redwordVoucher) {
        List<StockInOut> stockInOuts = stockInOutDao.findByVoucherAndVoucherType(voucher, voucherType);
        for (StockInOut stockInOut : stockInOuts) {
            entityManager.detach(stockInOut);
            stockInOut.setId(null);
            stockInOut.setVoucher(redwordVoucher);
            if (!stockInOut.getDiffPurchasingQuantity().equals(BigDecimal.ZERO)) {
                stockInOut.setDiffPurchasingQuantity(stockInOut.getDiffPurchasingQuantity().negate());
            }
            if (!stockInOut.getDiffSalingQuantity().equals(BigDecimal.ZERO)) {
                stockInOut.setDiffSalingQuantity(stockInOut.getDiffSalingQuantity().negate());
            }
            if (!stockInOut.getDiffQuantity().equals(BigDecimal.ZERO)) {
                stockInOut.setDiffQuantity(stockInOut.getDiffQuantity().negate());
            }
            saveCascade(stockInOut);
        }
    }

}
