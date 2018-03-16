package com.etrade.framework.core.sys.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.etrade.framework.core.auth.entity.Privilege;
import com.etrade.framework.core.auth.entity.Privilege.PrivilegeTypeEnum;
import com.etrade.framework.core.auth.service.PrivilegeService;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.dao.ReportDefDao;
import com.etrade.framework.core.rpt.entity.ReportDef;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.MenuDao;
import com.etrade.framework.core.sys.entity.Menu;
import com.etrade.framework.core.sys.entity.Menu.MenuTypeEnum;
import com.etrade.framework.core.sys.vo.NavMenuVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
@Transactional
public class MenuService extends BaseService<Menu, String> {

    private final static Logger logger = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private ReportDefDao reportDefDao;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private SecurityMetadataSource securityMetadataSource;

    @Override
    protected BaseDao<Menu, String> getEntityDao() {
        return menuDao;
    }

    @Transactional(readOnly = true)
    public List<Menu> findRoots() {
        List<Menu> roots = Lists.newArrayList();
        Iterable<Menu> allMenus = menuDao.findAllCached();
        for (Menu menu : allMenus) {
            if (menu.getParent() == null) {
                roots.add(menu);
            }
        }
        Collections.sort(roots);
        return roots;
    }

    @Transactional(readOnly = true)
    public List<Menu> findChildren(Menu parent) {
        List<Menu> items = Lists.newArrayList();
        Iterable<Menu> allMenus = menuDao.findAllCached();
        for (Menu menu : allMenus) {
            if (menu.getParent() == parent) {
                items.add(menu);
            }
        }
        Collections.sort(items);
        return items;
    }

    private void loopMenu(NavMenuVO parent, Set<GrantedAuthority> authorities, List<NavMenuVO> menuVOs, Menu menu,
            String currentWebContextPath) {
        if (menu.getDisabled()) {
            return;
        }
        NavMenuVO item = new NavMenuVO();
        menuVOs.add(item);
        item.setParent(parent);
        item.setId(menu.getCode());
        item.setCode(menu.getCode());
        item.setName(menu.getTitle());
        item.setIcon(menu.getStyle());
        item.setOpen(menu.getInitOpen());
        String menuURL = menu.getUrl();
        if (MenuTypeEnum.RELC.equals(menu.getType()) || menu.getType() == null) {
            // Change null to ""
            if (menuURL == null) {
                menuURL = "";
            }
            StringBuffer href = new StringBuffer(currentWebContextPath);
            if (StringUtils.isNotBlank(menuURL) && !menuURL.startsWith("/")) {
                href.append("/");
            }
            href.append(menuURL);
        } else if (MenuTypeEnum.RELD.equals(menu.getType())) {

        } else if (MenuTypeEnum.ABS.equals(menu.getType())) {

        }

        if (StringUtils.isNotBlank(menuURL)) {
            Collection<ConfigAttribute> configAttributes = securityMetadataSource.getAttributes(menuURL);
            if (CollectionUtils.isEmpty(configAttributes)) {
                item.setShow(true);
                logger.debug("No authority required for candidate menu url: {}", menuURL);
            } else {
                boolean matched = false;
                for (ConfigAttribute configAttribute : configAttributes) {
                    for (GrantedAuthority authority : authorities) {
                        if (authority.getAuthority().equals(configAttribute.getAttribute())) {
                            item.setShow(true);
                            logger.debug("Found matched and granted authority for URL: {}, authority: {}", menuURL,
                                    authority.getAuthority());
                            matched = true;
                            break;
                        }
                    }
                    if (matched) {
                        break;
                    }
                }
                if (!matched) {
                    logger.debug("Requird but NO granted authority for URL: {}", menuURL);
                }
            }

            item.setUrl(menuURL);
        }
        List<Menu> children = findChildren(menu);
        if (!CollectionUtils.isEmpty(children)) {
            List<NavMenuVO> childrenList = Lists.newArrayList();
            item.setChildren(childrenList);
            for (Menu child : children) {
                loopMenu(item, authorities, childrenList, child, currentWebContextPath);
            }
        }
    }

    private void loopFilterEmptyMenu(NavMenuVO parent, List<NavMenuVO> filteredMenuVOs, NavMenuVO menuVO) {
        NavMenuVO item = null;
        if (StringUtils.isNotBlank(menuVO.getUrl())) {
            if (menuVO.getShow()) {
                item = new NavMenuVO();
            }
        } else {
            List<NavMenuVO> children = menuVO.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                for (NavMenuVO navMenuVO : children) {
                    if (navMenuVO.getShow()) {
                        item = new NavMenuVO();
                        break;
                    }
                }
            }
        }
        if (item != null) {

            filteredMenuVOs.add(item);
            item.setId(menuVO.getId());
            item.setCode(menuVO.getCode());
            item.setName(menuVO.getName());
            item.setIcon(menuVO.getIcon());
            item.setOpen(menuVO.getOpen());
            item.setUrl(menuVO.getUrl());

            List<NavMenuVO> children = menuVO.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                boolean havaItemShow = false;
                for (NavMenuVO navMenuVO : children) {
                    if (navMenuVO.getShow()) {
                        havaItemShow = true;
                        break;
                    }
                }
                if (havaItemShow) {
                    List<NavMenuVO> childrenList = Lists.newArrayList();
                    item.setChildren(childrenList);
                    for (NavMenuVO child : children) {
                        loopFilterEmptyMenu(item, childrenList, child);
                    }
                }
            }
        }

    }

    @Transactional(readOnly = true)
    public List<NavMenuVO> authUserMenu(Set<GrantedAuthority> authorities, String currentWebContextPath) {
        List<NavMenuVO> menuVOs = new ArrayList<NavMenuVO>();
        List<Menu> roots = this.findRoots();
        for (Menu root : roots) {
            loopMenu(null, authorities, menuVOs, root, currentWebContextPath);
        }

        for (NavMenuVO root : menuVOs) {
            if (root.getId().equals(Menu.MENU_CODE_RPT)) {
                Map<String, List<ReportDef>> reportDefCategoryMap = Maps.newLinkedHashMap();
                List<ReportDef> reportDefs = reportDefDao.findDisplayItems();
                for (ReportDef reportDef : reportDefs) {
                    Collection<ConfigAttribute> configAttributes = securityMetadataSource.getAttributes(reportDef
                            .getReportAccessUrl());
                    boolean matched = false;
                    for (ConfigAttribute configAttribute : configAttributes) {
                        for (GrantedAuthority authority : authorities) {
                            if (authority.getAuthority().equals(configAttribute.getAttribute())) {
                                matched = true;
                                break;
                            }
                        }
                        if (matched) {
                            break;
                        }
                    }
                    if (!matched) {
                        logger.debug("Requird but NO granted role for report: {}", reportDef.getCode());
                    } else {
                        logger.debug("Found matched and granted role for report: {}", reportDef.getCode());
                        List<ReportDef> categoryReportDefs = reportDefCategoryMap.get(reportDef.getCategory());
                        if (categoryReportDefs == null) {
                            categoryReportDefs = Lists.newArrayList();
                            reportDefCategoryMap.put(reportDef.getCategory(), categoryReportDefs);
                        }
                        categoryReportDefs.add(reportDef);
                    }
                }

                for (Map.Entry<String, List<ReportDef>> me : reportDefCategoryMap.entrySet()) {
                    if (me.getValue().size() > 0) {
                        NavMenuVO item = new NavMenuVO();
                        item.setShow(true);
                        root.getChildren().add(item);
                        List<NavMenuVO> children = Lists.newArrayList();
                        item.setChildren(children);
                        item.setParent(root);
                        item.setId(UUID.randomUUID().toString());
                        item.setName(me.getKey());
                        item.setOpen(false);

                        for (ReportDef reportDef : me.getValue()) {
                            NavMenuVO itemInternal = new NavMenuVO();
                            itemInternal.setShow(true);
                            item.getChildren().add(itemInternal);
                            itemInternal.setParent(item);
                            itemInternal.setId(reportDef.getId());
                            itemInternal.setName(reportDef.getTitle());
                            itemInternal.setOpen(false);
                            itemInternal.setUrl(reportDef.getReportAccessUrl());
                        }
                    }
                }

                break;
            }
        }

        //���˵�����ʾ��û������Ŀ�Ĳ˵���
        List<NavMenuVO> filteredMenuVOs = new ArrayList<NavMenuVO>();
        for (NavMenuVO menuVO : menuVOs) {
            loopFilterEmptyMenu(null, filteredMenuVOs, menuVO);
        }

        return filteredMenuVOs;
    }

    @Override
    public Menu save(Menu entity) {
        if (entity.isNew()) {
            if (StringUtils.isBlank(entity.getCode())) {
                entity.setCode("M" + RandomStringUtils.randomNumeric(6));
            }
            //ͬ���Զ������Ķ�ӦȨ������
            String url = entity.getUrl();
            if (StringUtils.isNotBlank(url) && url.startsWith("/")) {
                if (privilegeService.findByProperty("url", url) == null) {
                    Privilege privilege = new Privilege();
                    privilege.setCategory("�˵�Ȩ��");
                    privilege.setType(PrivilegeTypeEnum.MENU);
                    privilege.setCode("P" + entity.getCode());
                    privilege.setTitle(entity.getTitle());
                    privilege.setDescription("�����˵��Զ�������ӦȨ��");
                    privilege.setUrl(entity.getUrl());
                    privilegeService.save(privilege);
                }
            }
        }
        return super.save(entity);
    }

    @Override
    public void delete(Menu entity) {
        //ͬ�������Զ������Ķ�ӦȨ������
        String url = entity.getUrl();
        if (StringUtils.isNotBlank(url)) {
            Privilege privilege = privilegeService.findByProperty("url", url);
            if (privilege != null) {
                privilegeService.delete(privilege);
            }
        }
        super.delete(entity);
    }

}
