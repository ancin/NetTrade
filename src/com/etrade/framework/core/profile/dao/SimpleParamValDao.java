package com.etrade.framework.core.profile.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.profile.entity.SimpleParamVal;

@Repository
public interface SimpleParamValDao extends BaseDao<SimpleParamVal, String> {
    SimpleParamVal findByUserAndCode(User user, String code);

    List<SimpleParamVal> findByUser(User user);
}