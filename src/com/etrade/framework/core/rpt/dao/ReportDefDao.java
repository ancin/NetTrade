package com.etrade.framework.core.rpt.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.entity.ReportDef;

@Repository
public interface ReportDefDao extends BaseDao<ReportDef, String> {

    @Query("from ReportDef where disabled!=true order by category asc, orderRank desc")
    @QueryHints({ @QueryHint(name = org.hibernate.ejb.QueryHints.HINT_CACHEABLE, value = "true") })
    List<ReportDef> findDisplayItems();
    
    ReportDef findByCode(String code);
    
    @Query("select distinct category from ReportDef order by category asc")
    List<String> findCategories();
}