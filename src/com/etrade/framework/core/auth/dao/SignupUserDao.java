package com.etrade.framework.core.auth.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.SignupUser;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface SignupUserDao extends BaseDao<SignupUser, String> {

}