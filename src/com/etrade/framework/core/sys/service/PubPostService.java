package com.etrade.framework.core.sys.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.PubPostDao;
import com.etrade.framework.core.sys.entity.PubPost;

@Service
@Transactional
public class PubPostService extends BaseService<PubPost, String> {

    private final Logger logger = LoggerFactory.getLogger(PubPostService.class);

    @Autowired
    private PubPostDao pubPostDao;

    @Override
    protected BaseDao<PubPost, String> getEntityDao() {
        return pubPostDao;
    }

    @Transactional(readOnly = true)
    @Cacheable("PubPostSpringCache")
    public List<PubPost> findPublished() {
        logger.debug("Finding published post message...");
        return pubPostDao.findPublished(new Date());
    }

    @Override
    @CacheEvict(value = "PubPostSpringCache", allEntries = true)
    public PubPost save(PubPost entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "PubPostSpringCache", allEntries = true)
    public List<PubPost> save(Iterable<PubPost> entities) {
        return super.save(entities);
    }

    @Override
    @CacheEvict(value = "PubPostSpringCache", allEntries = true)
    public void delete(PubPost entity) {
        super.delete(entity);
    }

    @Override
    @CacheEvict(value = "PubPostSpringCache", allEntries = true)
    public void delete(Iterable<PubPost> entities) {
        super.delete(entities);
    }

    @CacheEvict(value = "PubPostSpringCache", allEntries = true)
    public void evictCache() {
        //Just trigger spring cache evict
    }

}
