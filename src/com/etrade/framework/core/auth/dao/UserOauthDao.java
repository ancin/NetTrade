package com.etrade.framework.core.auth.dao;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.UserOauth;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface UserOauthDao extends BaseDao<UserOauth, String> {

	@QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
	UserOauth findByUsername(String username);
}
