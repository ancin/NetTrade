package com.etrade.biz.finance.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.biz.finance.entity.AccountSubject;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface AccountSubjectDao extends BaseDao<AccountSubject, Long> {

    @Query("from AccountSubject")
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    public List<AccountSubject> findAllCached();

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    public AccountSubject findByCode(String code);
}