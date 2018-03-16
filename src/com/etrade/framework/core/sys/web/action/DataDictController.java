package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.DataDict;
import com.etrade.framework.core.sys.service.DataDictService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "数据字典管理")
public class DataDictController extends BaseController<DataDict, String> {

    @Autowired
    private DataDictService dataDictService;

    @Override
    protected BaseService<DataDict, String> getEntityService() {
        return dataDictService;
    }

    @Override
    protected void checkEntityAclPermission(DataDict entity) {
        // Do nothing
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
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmpty()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent.id", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}