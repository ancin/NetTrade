package com.etrade.framework.core.sys.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.LoggingEvent;

@Repository
public interface LoggingEventDao extends BaseDao<LoggingEvent, Long> {

}