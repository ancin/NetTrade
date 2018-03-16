package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.ConfigProperty;
import com.etrade.framework.core.sys.service.ConfigPropertyService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "参数属性配置")
public class ConfigPropertyController extends BaseController<ConfigProperty, String> {

    @Autowired
    private ConfigPropertyService configPropertyService;

    @Override
    protected BaseService<ConfigProperty, String> getEntityService() {
        return configPropertyService;
    }

    @Override
    protected void checkEntityAclPermission(ConfigProperty entity) {
        // Nothing to do
    }

    @Override
    @MetaData(value = "保存")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData(value = "删除")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}