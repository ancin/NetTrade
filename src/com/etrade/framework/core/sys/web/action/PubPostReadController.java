package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.PubPostRead;
import com.etrade.framework.core.sys.service.PubPostReadService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "公告阅读记录")
public class PubPostReadController extends BaseController<PubPostRead, String> {

    @Autowired
    private PubPostReadService pubPostReadService;

    @Override
    protected BaseService<PubPostRead, String> getEntityService() {
        return pubPostReadService;
    }

    @Override
    protected void checkEntityAclPermission(PubPostRead entity) {
        // TODO Add acl check code logic
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}