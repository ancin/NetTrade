package com.etrade.framework.core.auth.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface UserLogonLogDao extends BaseDao<UserLogonLog, Long> {

    UserLogonLog findByHttpSessionId(String httpSessionId);
}