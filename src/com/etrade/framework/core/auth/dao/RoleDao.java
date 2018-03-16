package com.etrade.framework.core.auth.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface RoleDao extends BaseDao<Role, String> {
    @Query("from Role order by code asc")
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    List<Role> findAllCached();

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    Role findByCode(String code);
}