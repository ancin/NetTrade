package com.etrade.framework.core.sys.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.PubPost;

@Repository
public interface PubPostDao extends BaseDao<PubPost, String> {

    @Query("from PubPost t where t.publishTime is not null and t.publishTime<:currentDate and (t.expireTime>:currentDate or t.expireTime is null) order by t.publishTime desc")
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    List<PubPost> findPublished(@Param("currentDate") Date currentDate);
}