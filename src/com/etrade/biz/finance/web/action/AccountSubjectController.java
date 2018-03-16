package com.etrade.biz.finance.web.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.finance.entity.AccountSubject;
import com.etrade.biz.finance.service.AccountSubjectService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@MetaData("会计科目管理")
public class AccountSubjectController extends BaseController<AccountSubject, Long> {

    @Autowired
    private AccountSubjectService accountSubjectService;

    @Override
    protected BaseService<AccountSubject, Long> getEntityService() {
        return accountSubjectService;
    }

    @Override
    protected void checkEntityAclPermission(AccountSubject entity) {
        // TODO Add acl check code logic
    }

    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmpty()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent.id", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @Override
    @MetaData("保存")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData("删除")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData("查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData(value = "付款会计科目树形数据")
    @SecurityControlIgnore
    public HttpHeaders findPaymentAccountSubjects() {
        List<Map<String, Object>> treeDatas = Lists.newArrayList();
        Iterable<AccountSubject> items = accountSubjectService.findPaymentAccountSubjects();
        for (AccountSubject item : items) {
            loopTreeData(treeDatas, item);
        }
        setModel(treeDatas);
        return buildDefaultHttpHeaders();
    }

    private void loopTreeData(List<Map<String, Object>> treeDatas, AccountSubject item) {
        Map<String, Object> row = Maps.newHashMap();
        treeDatas.add(row);
        row.put("id", item.getId());
        row.put("code", item.getCode());
        row.put("name", item.getName());
        row.put("display", item.getDisplay());
        row.put("open", false);
        List<AccountSubject> children = accountSubjectService.findChildren(item);
        if (!CollectionUtils.isEmpty(children)) {
            List<Map<String, Object>> childrenList = Lists.newArrayList();
            row.put("children", childrenList);
            for (AccountSubject child : children) {
                loopTreeData(childrenList, child);
            }
        }
    }

}