package com.etrade.framework.core.sys.dao;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.ConfigProperty;

@Repository
public interface ConfigPropertyDao extends BaseDao<ConfigProperty, String> {

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    ConfigProperty findByPropKey(String propKey);
}