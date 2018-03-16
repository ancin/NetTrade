package com.etrade.framework.core.rpt.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.rpt.entity.ReportParam;
import com.etrade.framework.core.rpt.service.ReportDefService;
import com.etrade.framework.core.rpt.service.ReportParamService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "报表参数管理")
public class ReportParamController extends BaseController<ReportParam, String> {

    @Autowired
    private ReportParamService reportParamService;

    @Autowired
    private ReportDefService reportDefService;

    @Override
    protected BaseService<ReportParam, String> getEntityService() {
        return reportParamService;
    }

    @Override
    protected void checkEntityAclPermission(ReportParam entity) {
        // TODO Add acl check code logic
    }

    @Override
    @MetaData(value = "创建")
    public HttpHeaders doCreate() {
        String reportDefId = this.getRequiredParameter("reportDefId");
        bindingEntity.setReportDef(reportDefService.findOne(reportDefId));
        return super.doCreate();
    }

    @Override
    @MetaData(value = "更新")
    public HttpHeaders doUpdate() {
        return super.doUpdate();
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

    @Override
    public void prepareCreate() {
        super.prepareCreate();
        bindingEntity.setCode("RPT_");
    }
}