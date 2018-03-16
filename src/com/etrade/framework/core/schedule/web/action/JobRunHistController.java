package com.etrade.framework.core.schedule.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.schedule.entity.JobRunHist;
import com.etrade.framework.core.schedule.service.JobRunHistService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "定时任务运行记录")
public class JobRunHistController extends BaseController<JobRunHist,String> {

    @Autowired
    private JobRunHistService jobRunHistService;

    @Override
    protected BaseService<JobRunHist, String> getEntityService() {
        return jobRunHistService;
    }
    
    @Override
    protected void checkEntityAclPermission(JobRunHist entity) {
        // Nothing to do
    }
    
    @Override
    @MetaData(value = "删除")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}