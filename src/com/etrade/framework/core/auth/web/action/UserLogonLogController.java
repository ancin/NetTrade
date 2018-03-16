package com.etrade.framework.core.auth.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.auth.service.UserLogonLogService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "µÇÂ¼¼ÇÂ¼")
public class UserLogonLogController extends BaseController<UserLogonLog, Long> {

    @Autowired
    private UserLogonLogService userLogonLogService;

    @Override
    protected BaseService<UserLogonLog, Long> getEntityService() {
        return userLogonLogService;
    }

    @Override
    protected void checkEntityAclPermission(UserLogonLog entity) {
        // Allow all
    }

    @Override
    @MetaData(value = "²éÑ¯")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}