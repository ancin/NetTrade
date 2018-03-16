package com.etrade.framework.core.sys.dao;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.Menu;

@Repository
public interface MenuDao extends BaseDao<Menu, String> {

    @Query("from Menu")
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    public Iterable<Menu> findAllCached();
}
