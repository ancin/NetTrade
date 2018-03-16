package com.etrade.biz.stock.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.stock.entity.StockInOut;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface StockInOutDao extends BaseDao<StockInOut, Long> {
    List<StockInOut> findByVoucherAndVoucherType(String voucher, VoucherTypeEnum voucherType);
}