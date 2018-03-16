package com.etrade.framework.core.schedule.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.schedule.entity.JobRunHist;

@Repository
public interface JobRunHistDao extends BaseDao<JobRunHist, String> {

}