package com.etrade.framework.core.rpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.dao.ReportParamDao;
import com.etrade.framework.core.rpt.entity.ReportParam;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class ReportParamService extends BaseService<ReportParam,String>{
    
    @Autowired
    private ReportParamDao reportParamDao;

    @Override
    protected BaseDao<ReportParam, String> getEntityDao() {
        return reportParamDao;
    }
}
