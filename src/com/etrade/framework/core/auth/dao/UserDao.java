package com.etrade.framework.core.auth.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface UserDao extends BaseDao<User, Long> {

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    List<User> findBySigninid(String signid);

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    User findByUid(String uid);

    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    @Query("select count(*) from User")
    Long findUserCount();

    List<User> findByAclCode(String aclCode);
}
