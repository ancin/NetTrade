package com.etrade.framework.core.auth.dao;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.dao.BaseDao;

@Repository
public interface DepartmentDao extends BaseDao<Department, String> {

}
