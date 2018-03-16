package com.etrade.framework.core.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.dao.DepartmentDao;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;

@Service
@Transactional
public class DepartmentService extends BaseService<Department, String> {

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    protected BaseDao<Department, String> getEntityDao() {
        return departmentDao;
    }

}
