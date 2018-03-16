package com.etrade.framework.core.schedule.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.schedule.entity.JobBeanCfg;

@Repository
public interface JobBeanCfgDao extends BaseDao<JobBeanCfg, String> {

    @Query("from JobBeanCfg")
    List<JobBeanCfg> findAll();
    
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    JobBeanCfg findByJobClass(String jobClass);
}