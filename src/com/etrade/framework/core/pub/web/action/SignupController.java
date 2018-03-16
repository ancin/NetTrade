package com.etrade.framework.core.pub.web.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.auth.entity.SignupUser;
import com.etrade.framework.core.auth.service.SignupUserService;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.captcha.ImageCaptchaServlet;
import com.etrade.framework.core.web.view.OperationResult;

/**
 * 账号注册
 */
public class SignupController extends BaseController<SignupUser, String> {

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private SignupUserService signupUserService;

    public HttpHeaders submit() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String jCaptcha = getRequiredParameter("j_captcha");
        if (!ImageCaptchaServlet.validateResponse(request, jCaptcha)) {
            setModel(OperationResult.buildFailureResult("验证码不正确，请重新输入"));
            return buildDefaultHttpHeaders();
        }
        
        if (dynamicConfigService.isSignupDisabled()) {
            setModel(OperationResult.buildFailureResult("系统暂未开发账号注册功能，如有疑问请联系管理员"));
            return buildDefaultHttpHeaders();
        }

        String signinid = bindingEntity.getSigninid();

        if (signupUserService.findByProperty("signinid", signinid) != null) {
            setModel(OperationResult.buildFailureResult("注册账号:" + signinid + " 已被注册使用，请修改使用其他账号"));
            return buildDefaultHttpHeaders();
        }

        if (userService.findByProperty("signinid", signinid) != null) {
            setModel(OperationResult.buildFailureResult("注册账号:" + signinid + " 已被注册使用，请修改使用其他账号"));
            return buildDefaultHttpHeaders();
        }

        String email = bindingEntity.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (signupUserService.findByProperty("email", email) != null) {
                setModel(OperationResult
                    .buildFailureResult("注册邮件:" + email + " 已被注册使用，请修改使用其他电子邮件"));
                return buildDefaultHttpHeaders();
            }

            if (userService.findByProperty("email", email) != null) {
                setModel(OperationResult
                    .buildFailureResult("注册邮件:" + email + " 已被注册使用，请修改使用其他电子邮件"));
                return buildDefaultHttpHeaders();
            }
        }

        signupUserService.save(bindingEntity);

        setModel(OperationResult.buildSuccessResult("账号注册成功"));
        return buildDefaultHttpHeaders();
    }

    @Override
    protected BaseService<SignupUser, String> getEntityService() {
        return signupUserService;
    }

    @Override
    protected void checkEntityAclPermission(SignupUser entity) {
        //Do nothing
    }
}
