package com.etrade.framework.core.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.PubPost;
import com.etrade.framework.core.sys.entity.PubPostRead;

@Repository
public interface PubPostReadDao extends BaseDao<PubPostRead, String> {

    @Query("from PubPostRead t where t.readUser=:readUser and t.pubPost in (:pubPosts)")
    List<PubPostRead> findReaded(@Param("readUser") User readUser, @Param("pubPosts") List<PubPost> pubPosts);

    PubPostRead findByReadUserAndPubPost(User readUser, PubPost pubPost);
}