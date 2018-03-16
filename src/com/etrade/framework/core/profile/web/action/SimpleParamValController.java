package com.etrade.framework.core.profile.web.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.security.AuthUserHolder;
import com.etrade.framework.core.profile.entity.SimpleParamVal;
import com.etrade.framework.core.profile.service.SimpleParamValService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Maps;

@MetaData("���Ի����ò������ݹ���")
public class SimpleParamValController extends BaseController<SimpleParamVal, String> {

    @Autowired
    private SimpleParamValService simpleParamValService;

    @Override
    protected BaseService<SimpleParamVal, String> getEntityService() {
        return simpleParamValService;
    }

    @Override
    protected void checkEntityAclPermission(SimpleParamVal entity) {
        // TODO Add acl check code logic
    }

    @Override
    @MetaData("����")
    public HttpHeaders doSave() {
        String[] codes = getRequiredParameter("codes").split(",");
        User user = AuthUserHolder.getLogonUser();
        for (String code : codes) {
            SimpleParamVal entity = simpleParamValService.findByUserAndCode(user, code);
            if (entity == null) {
                entity = new SimpleParamVal();
                entity.setUser(user);
            }
            entity.setCode(code);
            entity.setValue(getRequiredParameter(code));
            getEntityService().save(entity);
        }
        setModel(OperationResult.buildSuccessResult("����Ĭ��ֵ�趨�ɹ�"));
        return buildDefaultHttpHeaders();
    }

    @MetaData("�����б�")
    @SecurityControlIgnore
    public HttpHeaders params() {
        User user = AuthUserHolder.getLogonUser();
        Map<String, String> datas = Maps.newHashMap();
        List<SimpleParamVal> simpleParams = simpleParamValService.findByUser(user);
        for (SimpleParamVal simpleParamVal : simpleParams) {
            datas.put(simpleParamVal.getCode(), simpleParamVal.getValue());
        }
        setModel(datas);
        return buildDefaultHttpHeaders();
    }
}