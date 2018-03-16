package com.etrade.biz.md.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.md.service.CommodityService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("商品管理")
public class CommodityController extends BaseController<Commodity, String> {

    @Autowired
    private CommodityService commodityService;

    @Override
    protected BaseService<Commodity, String> getEntityService() {
        return commodityService;
    }

    @Override
    protected void checkEntityAclPermission(Commodity entity) {
        // TODO Add acl check code logic
    }

    @MetaData("[TODO方法作用]")
    public HttpHeaders todo() {
        //TODO
        setModel(OperationResult.buildSuccessResult("TODO操作完成"));
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData("创建")
    public HttpHeaders doCreate() {
        return super.doCreate();
    }

    @Override
    @MetaData("更新")
    public HttpHeaders doUpdate() {
        return super.doUpdate();
    }

    @Override
    @MetaData("保存")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData("删除")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData("查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}