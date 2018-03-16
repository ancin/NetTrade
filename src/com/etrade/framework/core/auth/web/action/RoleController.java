package com.etrade.framework.core.auth.web.action;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Privilege;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.RoleR2Privilege;
import com.etrade.framework.core.auth.service.PrivilegeService;
import com.etrade.framework.core.auth.service.RoleService;
import com.etrade.framework.core.exception.DataAccessDeniedException;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.security.AclService;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@MetaData(value = "��ɫ����")
public class RoleController extends BaseController<Role, String> {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired(required = false)
    private AclService aclService;

    @Override
    protected BaseService<Role, String> getEntityService() {
        return roleService;
    }

    @Override
    protected void checkEntityAclPermission(Role entity) {
        //Do nothing check
    }

    public Map<String, String> getAclTypeMap() {
        Map<String, String> aclTypeMap = Maps.newLinkedHashMap();
        if (aclService != null) {
            String authUserAclType = AuthContextHolder.getAuthUserDetails().getAclType();
            if (authUserAclType == null) {
                aclTypeMap = aclService.getAclTypeMap();
            } else {
                Map<String, String> globalAclTypeMap = aclService.getAclTypeMap();
                for (String aclType : globalAclTypeMap.keySet()) {
                    if (authUserAclType.compareTo(aclType) > 0) {
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
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @Override
    protected String isDisallowDelete(Role entity) {
        if (entity.getCode().equals(Role.ROLE_ADMIN_CODE) || entity.getCode().equals(Role.ROLE_ANONYMOUSLY_CODE)) {
            return "ϵͳԤ�����ݣ�������ɾ��:" + entity.getDisplay();
        }
        return null;
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doSave() {
        String authUserAclType = AuthContextHolder.getAuthUserDetails().getAclType();
        if (StringUtils.isNotBlank(authUserAclType)) {
            //�ж�ѡȡ�������Ƿ����ڵ�ǰ��¼�û���Ͻ��Χ
            String aclType = this.getRequiredParameter("aclType");
            if (authUserAclType.compareTo(aclType) < 0) {
                throw new DataAccessDeniedException("���ݷ���Ȩ�޲���");
            }
            bindingEntity.setAclType(aclType);
        }
        return super.doSave();
    }

    @MetaData(value = "��������״̬")
    public HttpHeaders doState() {
        boolean disabled = BooleanUtils.toBoolean(this.getRequiredParameter("disabled"));
        Collection<Role> entities = this.getEntitiesByParameterIds();
        for (Role entity : entities) {
            entity.setDisabled(disabled);
        }
        getEntityService().save(entities);
        setModel(OperationResult.buildSuccessResult("��������״̬�����ɹ�"));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "Ȩ�޹���")
    @SecurityControlIgnore
    public HttpHeaders privileges() {
        Map<String, List<Privilege>> groupDatas = Maps.newLinkedHashMap();
        List<Privilege> privileges = privilegeService.findAllCached();
        List<RoleR2Privilege> r2s = roleService.findOne(this.getId()).getRoleR2Privileges();
        for (Privilege privilege : privileges) {
            List<Privilege> groupPrivileges = groupDatas.get(privilege.getCategory());
            if (groupPrivileges == null) {
                groupPrivileges = Lists.newArrayList();
                groupDatas.put(privilege.getCategory(), groupPrivileges);
            }
            groupPrivileges.add(privilege);
            privilege.addExtraAttribute("related", false);
            for (RoleR2Privilege r2 : r2s) {
                if (r2.getPrivilege().equals(privilege)) {
                    privilege.addExtraAttribute("r2CreatedDate", r2.getCreatedDate());
                    privilege.addExtraAttribute("related", true);
                    break;
                }
            }
        }
        this.getRequest().setAttribute("privileges", groupDatas);
        return buildDefaultHttpHeaders("privileges");
    }

    @MetaData(value = "����Ȩ�޹���")
    public HttpHeaders doUpdateRelatedPrivilegeR2s() {
        roleService.updateRelatedPrivilegeR2s(getId(), getParameterIds("r2ids"));
        setModel(OperationResult.buildSuccessResult("����Ȩ�޹����������"));
        return buildDefaultHttpHeaders();
    }

    /**
     * �������׷�ӹ���������������ڷ�����һ����ڵ�ǰ��¼�û�ǿ��׷�ӹ�������
     * 
     * @param filters
     */
    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        //�޶���ѯACL��Ͻ��Χ����
        String authUserAclType = AuthContextHolder.getAuthUserDetails().getAclType();
        if (StringUtils.isNotBlank(authUserAclType)) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.LE, "aclType", authUserAclType));
        }
    }

    @MetaData("��ɫ�����û�")
    public HttpHeaders users() {
        this.getRequest().setAttribute("users", bindingEntity.getRoleR2Users());
        return buildDefaultHttpHeaders("users");
    }
}