package com.etrade.framework.core.rpt.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.entity.ReportDefR2Role;

@Repository
public interface ReportDefR2RoleDao extends BaseDao<ReportDefR2Role, String> {

    List<ReportDefR2Role> findByRole_Id(String roleId);
    
    List<ReportDefR2Role> findByReportDef_Id(String reportDefId);
}
