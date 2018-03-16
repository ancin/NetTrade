package com.etrade.framework.core.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.dao.UserLogonLogDao;
import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class UserLogonLogService extends BaseService<UserLogonLog,Long>{
    
    @Autowired
    private UserLogonLogDao userLogonLogDao;

    @Override
    protected BaseDao<UserLogonLog, Long> getEntityDao() {
        return userLogonLogDao;
    }
    
    public UserLogonLog findBySessionId(String httpSessionId){
        return userLogonLogDao.findByHttpSessionId(httpSessionId);
    }
}
