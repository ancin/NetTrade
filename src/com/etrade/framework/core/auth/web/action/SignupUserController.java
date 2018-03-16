package com.etrade.framework.core.auth.web.action;

import java.util.List;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.SignupUser;
import com.etrade.framework.core.auth.service.RoleService;
import com.etrade.framework.core.auth.service.SignupUserService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("注册账号管理")
public class SignupUserController extends BaseController<SignupUser, String> {

    @Autowired
    private SignupUserService signupUserService;

    @Autowired
    private RoleService roleService;

    @Override
    protected BaseService<SignupUser, String> getEntityService() {
        return signupUserService;
    }

    @Override
    protected void checkEntityAclPermission(SignupUser entity) {
        // Nothing to do
    }

    public List<Role> getRoles() {
        return roleService.findAllCached();
    }

    @MetaData("审核")
    public HttpHeaders doAudit() {
        String aclCode = this.getParameter("aclCode");
        bindingEntity.setAclCode(aclCode);
        signupUserService.audit(bindingEntity, roleService.findAll(getParameterIds("r2ids")));
        setModel(OperationResult.buildSuccessResult("已审核处理并创建对应用户记录", bindingEntity));
        return buildDefaultHttpHeaders();
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
}