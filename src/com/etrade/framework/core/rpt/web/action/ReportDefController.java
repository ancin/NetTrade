package com.etrade.framework.core.rpt.web.action;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.service.RoleService;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.rpt.entity.ReportDef;
import com.etrade.framework.core.rpt.entity.ReportDefR2Role;
import com.etrade.framework.core.rpt.service.ReportDefService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.service.AttachmentFileService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData(value = "���������")
public class ReportDefController extends BaseController<ReportDef, String> {

    @Autowired
    private ReportDefService reportDefService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Override
    protected BaseService<ReportDef, String> getEntityService() {
        return reportDefService;
    }

    @Override
    protected void checkEntityAclPermission(ReportDef entity) {
        // According to URL access control logic
    }

    @Override
    public void prepareCreate() {
        super.prepareCreate();
        bindingEntity.setCode("RPT_" + RandomStringUtils.randomNumeric(6));
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doCreate() {
        String templateFileId = this.getParameter("templateFileId");
        if (StringUtils.isNotBlank(templateFileId)) {
            bindingEntity.setTemplateFile(attachmentFileService.findOne(templateFileId));
        }
        return super.doCreate();
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doUpdate() {
        String templateFileId = this.getParameter("templateFileId");
        if (StringUtils.isNotBlank(templateFileId)) {
            bindingEntity.setTemplateFile(attachmentFileService.findOne(templateFileId));
        } else {
            bindingEntity.setTemplateFile(null);
        }
        return super.doUpdate();
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    public List<String> getCategories() {
        return reportDefService.findCategories();
    }

    @MetaData(value = "������ʾ��ɫ��������")
    @SecurityControlIgnore
    public HttpHeaders findRelatedRoles() {
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(Role.class, getRequest());
        List<Role> roles = roleService.findByFilters(groupFilter, new Sort(Direction.DESC, "aclType", "code"));
        List<ReportDefR2Role> r2s = reportDefService.findRelatedRoleR2s(this.getId());
        for (Role role : roles) {
            role.addExtraAttribute("related", false);
            for (ReportDefR2Role r2 : r2s) {
                if (r2.getRole().equals(role)) {
                    role.addExtraAttribute("r2CreatedDate", r2.getCreatedDate());
                    role.addExtraAttribute("related", true);
                    break;
                }
            }
        }
        setModel(buildPageResultFromList(roles));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "���½�ɫ����")
    public HttpHeaders doUpdateRelatedRoleR2s() {
        reportDefService.updateRelatedRoleR2s(getId(), getParameterIds("r2ids"));
        setModel(OperationResult.buildSuccessResult("��ɫ�����������"));
        return buildDefaultHttpHeaders();
    }
}