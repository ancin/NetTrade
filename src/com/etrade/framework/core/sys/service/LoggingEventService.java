package com.etrade.framework.core.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.LoggingEventDao;
import com.etrade.framework.core.sys.entity.LoggingEvent;

@Service
@Transactional
public class LoggingEventService extends BaseService<LoggingEvent,Long>{
    
    @Autowired
    private LoggingEventDao loggingEventDao;

    @Override
    protected BaseDao<LoggingEvent, Long> getEntityDao() {
        return loggingEventDao;
    }
}
