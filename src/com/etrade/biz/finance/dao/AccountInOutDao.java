package com.etrade.biz.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.finance.entity.AccountInOut;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface AccountInOutDao extends BaseDao<AccountInOut, Long> {

    List<AccountInOut> findByVoucherAndVoucherType(String voucher, VoucherTypeEnum voucherType);

}