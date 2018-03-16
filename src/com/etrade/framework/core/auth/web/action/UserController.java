package com.etrade.framework.core.auth.web.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Privilege;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.entity.UserR2Role;
import com.etrade.framework.core.auth.service.PrivilegeService;
import com.etrade.framework.core.auth.service.RoleService;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.security.AclService;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.sys.service.MenuService;
import com.etrade.framework.core.sys.vo.NavMenuVO;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.json.ValueLabelBean;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UserController extends BaseController<User, Long> {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private MenuService menuService;

    @Autowired(required = false)
    private AclService aclService;

    @Override
    protected void checkEntityAclPermission(User entity) {
        if (aclService != null) {
            aclService.validateAuthUserAclCodePermission(entity.getAclCode());
        }
    }

    public Map<String, String> getAclTypeMap() {
        Map<String, String> aclTypeMap = Maps.newLinkedHashMap();
        if (aclService != null) {
            String authUserAclType = AuthContextHolder.getAuthUserDetails().getAclType();
            if (StringUtils.isBlank(authUserAclType)) {
                aclTypeMap = aclService.getAclTypeMap();
            } else {
                Map<String, String> globalAclTypeMap = aclService.getAclTypeMap();
                for (String aclType : globalAclTypeMap.keySet()) {
                    if (authUserAclType.compareTo(aclType) >= 0) {
                        aclTypeMap.put(aclType, globalAclTypeMap.get(aclType));
                    }
                }
            }
        }
        return aclTypeMap;
    }

    @SecurityControlIgnore
    public HttpHeaders aclTypeMapData() {
        setModel(getAclTypeMap());
        return buildDefaultHttpHeaders();
    }

    @Override
    protected BaseService<User, Long> getEntityService() {
        return userService;
    }

    @MetaData(value = "��ɫ����")
    @SecurityControlIgnore
    public HttpHeaders roles() {
        List<Role> roles = Lists.newArrayList();
        List<Role> allRoles = roleService.findAllCached();
        List<UserR2Role> r2s = userService.findOne(this.getId()).getUserR2Roles();
        for (Role role : allRoles) {
            if (Role.ROLE_ANONYMOUSLY_CODE.equals(role.getCode())) {
                continue;
            }
            role.addExtraAttribute("related", false);
            for (UserR2Role r2 : r2s) {
                if (r2.getRole().equals(role)) {
                    role.addExtraAttribute("r2CreatedDate", r2.getCreatedDate());
                    role.addExtraAttribute("related", true);
                    break;
                }
            }
            roles.add(role);
        }
        this.getRequest().setAttribute("roles", roles);
        return buildDefaultHttpHeaders("roles");
    }

    @MetaData(value = "���½�ɫ����")
    public HttpHeaders doUpdateRelatedRoleR2s() {
        if (bindingEntity.getSigninid().equals("admin")) {
            Validation.notDemoMode();
        }
        userService.updateRelatedRoleR2s(getId(), getParameterIds("r2ids"));
        setModel(OperationResult.buildSuccessResult("���½�ɫ�����������"));
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doSave() {
        if (bindingEntity.getSigninid().equals("admin")) {
            Validation.notDemoMode();
        }
        /**
         * �ж�ѡȡ���û����������Ƿ����ڵ�ǰ��¼�û���Ͻ��Χ
         * �������趨Ϊ�������Զ��󶨣�����Ҫ�ֹ������������ȡ���� *.auth.entity.User#setAclCode
         */
        String aclCode = this.getParameter("aclCode");
        bindingEntity.setAclCode(aclCode);
        if (bindingEntity.isNew()) {
            userService.save(bindingEntity, this.getRequiredParameter("newpassword"));
            setModel(OperationResult.buildSuccessResult("���������ɹ�", bindingEntity));
        } else {
            String newpassword = this.getParameter("newpassword");
            if (StringUtils.isNotBlank(newpassword)) {
                userService.save(bindingEntity, newpassword);
            } else {
                userService.save(bindingEntity);
            }
            setModel(OperationResult.buildSuccessResult("���²����ɹ�", bindingEntity));
        }
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(entityClass, getRequest());
        if (AuthContextHolder.getAuthUserDetails() != null) {
            Collection<String> aclCodePrefixs = AuthContextHolder.getAuthUserDetails().getAclCodePrefixs();
            if (!CollectionUtils.isEmpty(aclCodePrefixs)) {
                groupFilter.forceAnd(new PropertyFilter(MatchType.ACLPREFIXS, "aclCode", aclCodePrefixs));
            }
            String authUserAclType = AuthContextHolder.getAuthUserDetails().getAclType();
            if (StringUtils.isNotBlank(authUserAclType)) {
                groupFilter.forceAnd(new PropertyFilter(MatchType.LE, "aclType", authUserAclType));
            }
        }
        Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(getRequest());
        Page<User> page = this.getEntityService().findByPage(groupFilter, pageable);
        if (aclService != null) {
            Map<String, String> globalAclTypeMap = aclService.getAclTypeMap();
            for (User user : page.getContent()) {
                user.addExtraAttribute("aclTypeLabel", globalAclTypeMap.get(user.getAclType()));
            }
        }
        setModel(page);
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        if (bindingEntity.getSigninid().equals("admin")) {
            Validation.notDemoMode();
        }
        return super.doDelete();
    }

    @MetaData(value = "����ѡȡ��Autocomplete����")
    @SecurityControlIgnore
    public HttpHeaders aclCodes() {
        List<ValueLabelBean> lvList = Lists.newArrayList();
        if (aclService != null) {
            String term = this.getParameter("term");
            if (term != null && term.length() >= 2) {
                Map<String, String> keyValueMap = aclService.findAclCodesMap();
                Collection<String> aclCodePrefixs = AuthContextHolder.getAuthUserDetails().getAclCodePrefixs();

                for (Map.Entry<String, String> me : keyValueMap.entrySet()) {
                    String key = me.getKey();
                    if (key.startsWith(term)) {
                        for (String aclCodePrefix : aclCodePrefixs) {
                            if (key.startsWith(aclCodePrefix)) {
                                lvList.add(new ValueLabelBean(me.getKey(), me.getValue()));
                            }
                        }
                    }
                }
            }
        }
        setModel(lvList);
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "�����û�����Ȩ�޼���")
    public HttpHeaders privileges() {
        List<Privilege> privileges = privilegeService.findAllCached();
        List<Role> roles = roleService.findR2RolesForUser(bindingEntity);
        boolean isAdmin = false;
        for (Role role : roles) {
            if (role.getCode().equals(Role.ROLE_ADMIN_CODE)) {
                isAdmin = true;
                break;
            }
        }

        Map<String, List<Privilege>> groupDatas = Maps.newLinkedHashMap();
        List<Privilege> r2s = null;
        if (isAdmin) {
            r2s = privileges;
        } else {
            r2s = userService.findRelatedPrivilegesForUser(bindingEntity);
        }
        for (Privilege privilege : privileges) {
            List<Privilege> groupPrivileges = groupDatas.get(privilege.getCategory());
            if (groupPrivileges == null) {
                groupPrivileges = Lists.newArrayList();
                groupDatas.put(privilege.getCategory(), groupPrivileges);
            }
            groupPrivileges.add(privilege);
            privilege.addExtraAttribute("related", false);
            if (r2s.contains(privilege)) {
                privilege.addExtraAttribute("related", true);
            }
        }
        this.getRequest().setAttribute("privileges", groupDatas);
        return buildDefaultHttpHeaders("privileges");
    }

    @MetaData(value = "�����û������˵�����")
    public HttpHeaders menus() {
        Set<GrantedAuthority> authsSet = new HashSet<GrantedAuthority>();
        List<Role> roles = roleService.findR2RolesForUser(bindingEntity);
        for (Role role : roles) {
            authsSet.add(new SimpleGrantedAuthority(role.getCode()));
        }
        List<NavMenuVO> menus = menuService.authUserMenu(authsSet, this.getRequest().getContextPath());
        setModel(menus);
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData(value = "�汾�����б�")
    public HttpHeaders revisionList() {
        return super.revisionList();
    }

    @Override
    @MetaData(value = "�汾���ݶԱ�")
    public HttpHeaders revisionCompare() {
        return super.revisionCompare();
    }

    public HttpHeaders resetActivitiIndentityData() {
        userService.resetActivitiIndentityData();
        setModel(OperationResult.buildSuccessResult("�������û�Ⱥ���������ò������"));
        return buildDefaultHttpHeaders();
    }
}
