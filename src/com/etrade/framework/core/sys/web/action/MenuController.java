package com.etrade.framework.core.sys.web.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.sys.entity.Menu;
import com.etrade.framework.core.sys.service.MenuService;
import com.etrade.framework.core.web.BaseController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MenuController extends BaseController<Menu, String> {

    @Autowired
    private MenuService menuService;

    @Override
    protected BaseService<Menu, String> getEntityService() {
        return menuService;
    }

    @Override
    protected void checkEntityAclPermission(Menu entity) {
        //Do nothing check
    }

    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmpty()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @Override
    @MetaData(value = "保存")
    public HttpHeaders doSave() {
        Validation.notDemoMode();
        return super.doSave();
    }

    @Override
    @MetaData(value = "删除")
    public HttpHeaders doDelete() {
        Validation.notDemoMode();
        return super.doDelete();
    }

    @MetaData(value = "列表")
    public HttpHeaders list() {
        List<Map<String, Object>> menuList = Lists.newArrayList();
        Iterable<Menu> menus = menuService.findRoots();
        for (Menu menu : menus) {
            loopMenu(menuList, menu);
        }
        setModel(menuList);
        return buildDefaultHttpHeaders();
    }

    private void loopMenu(List<Map<String, Object>> menuList, Menu menu) {
        Map<String, Object> row = Maps.newHashMap();
        menuList.add(row);
        row.put("id", menu.getId());
        row.put("name", menu.getTitle());
        row.put("open", menu.getInitOpen());
        row.put("disabled", menu.getDisabled());
        List<Menu> children = menu.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            List<Map<String, Object>> childrenList = Lists.newArrayList();
            row.put("children", childrenList);
            for (Menu child : children) {
                loopMenu(childrenList, child);
            }
        }
    }
}
