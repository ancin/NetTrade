package com.etrade.biz.finance.web.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.finance.entity.BizTradeUnit;
import com.etrade.biz.finance.service.BizTradeUnitService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@MetaData("ҵ��������λ����")
public class BizTradeUnitController extends BaseController<BizTradeUnit, Long> {

    @Autowired
    private BizTradeUnitService bizTradeUnitService;

    @Override
    protected BaseService<BizTradeUnit, Long> getEntityService() {
        return bizTradeUnitService;
    }

    @Override
    protected void checkEntityAclPermission(BizTradeUnit entity) {
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
    public HttpHeaders doCreate() {
        return super.doCreate();
    }

    @Override
    @MetaData("����")
    public HttpHeaders doUpdate() {
        return super.doUpdate();
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

    @MetaData(value = "��������")
    @SecurityControlIgnore
    public HttpHeaders frequentUsedDatas() {
        Set<Map<String, Object>> datas = Sets.newHashSet();
        String type = getParameter("type");
        List<BizTradeUnit> entities = bizTradeUnitService.findFrequentUsedDatas();
        for (BizTradeUnit entity : entities) {
            if (StringUtils.isNotBlank(type)) {
                if (!type.equals(entity.getType().name())) {
                    continue;
                }
            }
            Map<String, Object> item = Maps.newHashMap();
            item.put("id", entity.getId());
            item.put("display", entity.getDisplay());
            datas.add(item);
        }
        setModel(datas);
        return buildDefaultHttpHeaders();
    }
}