package com.etrade.framework.core.profile.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.profile.dao.SimpleParamValDao;
import com.etrade.framework.core.profile.entity.SimpleParamVal;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class SimpleParamValService extends BaseService<SimpleParamVal, String> {

    @Autowired
    private SimpleParamValDao simpleParamValDao;

    @Override
    protected BaseDao<SimpleParamVal, String> getEntityDao() {
        return simpleParamValDao;
    }

    public SimpleParamVal findByUserAndCode(User user, String code) {
        return simpleParamValDao.findByUserAndCode(user, code);
    }

    public List<SimpleParamVal> findByUser(User user) {
        return simpleParamValDao.findByUser(user);
    }
}
