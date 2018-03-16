package com.etrade.framework.core.profile.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.profile.entity.ProfileParamDef;
import com.etrade.framework.core.profile.service.ProfileParamDefService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("���Ի����ò����������")
public class ProfileParamDefController extends BaseController<ProfileParamDef, String> {

    @Autowired
    private ProfileParamDefService profileParamDefService;

    @Override
    protected BaseService<ProfileParamDef, String> getEntityService() {
        return profileParamDefService;
    }

    @Override
    protected void checkEntityAclPermission(ProfileParamDef entity) {
        // TODO Add acl check code logic
    }

    @MetaData("[TODO��������]")
    public HttpHeaders todo() {
        //TODO
        setModel(OperationResult.buildSuccessResult("TODO�������"));
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData("����")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData("ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData("��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }
}