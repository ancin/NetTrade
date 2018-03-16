package com.etrade.biz.stock.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.stock.entity.StockInOut;
import com.etrade.biz.stock.service.StockInOutService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData("StockInOutController")
public class StockInOutController extends BaseController<StockInOut, Long> {

    @Autowired
    private StockInOutService stockInOutService;

    @Override
    protected BaseService<StockInOut, Long> getEntityService() {
        return stockInOutService;
    }

    @Override
    protected void checkEntityAclPermission(StockInOut entity) {

    }

    @Override
    @MetaData("≤È—Ø")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

}