package com.etrade.framework.core.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.PubPostReadDao;
import com.etrade.framework.core.sys.entity.PubPost;
import com.etrade.framework.core.sys.entity.PubPostRead;

@Service
@Transactional
public class PubPostReadService extends BaseService<PubPostRead, String> {

    @Autowired
    private PubPostReadDao pubPostReadDao;

    @Override
    protected BaseDao<PubPostRead, String> getEntityDao() {
        return pubPostReadDao;
    }

    public List<PubPostRead> findReaded(User readUser, List<PubPost> pubPosts) {
        return pubPostReadDao.findReaded(readUser, pubPosts);
    }

    public PubPostRead findReaded(User readUser, PubPost pubPost) {
        return pubPostReadDao.findByReadUserAndPubPost(readUser, pubPost);
    }
}
