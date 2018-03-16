package com.etrade.framework.core.rpt.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.entity.ReportParam;

@Repository
public interface ReportParamDao extends BaseDao<ReportParam, String> {

}