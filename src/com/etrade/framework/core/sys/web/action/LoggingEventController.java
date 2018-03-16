package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.common.PersistableController;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.LoggingEvent;
import com.etrade.framework.core.sys.service.LoggingEventService;

@MetaData(value = "日志处理")
public class LoggingEventController extends PersistableController<LoggingEvent,Long> {

    @Autowired
    private LoggingEventService loggingEventService;

    @Override
    protected BaseService<LoggingEvent, Long> getEntityService() {
        return loggingEventService;
    }
    
    @Override
    protected void checkEntityAclPermission(LoggingEvent entity) {
        // TODO Add acl check code logic
    }

    @Override
    @MetaData(value = "更新")
    public HttpHeaders doUpdate() {
        return super.doUpdate();
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