package com.etrade.framework.core.auth.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.service.DepartmentService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "���Ź���")
public class DepartmentController extends BaseController<Department, String> {

    @Autowired
    private DepartmentService departmentService;

    @Override
    protected BaseService<Department, String> getEntityService() {
        return departmentService;
    }

    @Override
    protected void checkEntityAclPermission(Department entity) {
        //Do nothing check
    }

    @Override
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doSave() {
        return super.doSave();
    }
}