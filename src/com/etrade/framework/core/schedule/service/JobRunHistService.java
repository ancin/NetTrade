package com.etrade.framework.core.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.schedule.dao.JobRunHistDao;
import com.etrade.framework.core.schedule.entity.JobRunHist;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class JobRunHistService extends BaseService<JobRunHist,String>{
    
    @Autowired
    private JobRunHistDao jobRunHistDao;

    @Override
    protected BaseDao<JobRunHist, String> getEntityDao() {
        return jobRunHistDao;
    }
    
    @Async
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void saveWithAsyncAndNewTransition(JobRunHist entity) {
        jobRunHistDao.save(entity);
    }
}
