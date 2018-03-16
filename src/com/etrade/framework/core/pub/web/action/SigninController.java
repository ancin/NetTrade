package com.etrade.framework.core.pub.web.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.ctx.MailService;
import com.etrade.framework.core.service.PropertiesConfigService;
import com.etrade.framework.core.web.captcha.ImageCaptchaServlet;
import com.etrade.framework.core.web.view.OperationResult;

/**
 * ��¼����
 */
public class SigninController extends SimpleController {

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Override
    public HttpHeaders index() {
        return new DefaultHttpHeaders("/pub/signin").disableCaching();
    }

    // @RequestMapping(value = "/index")
    /* public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

         return new ModelAndView("/pub/signin");
     }*/

    public boolean isDevMode() {
        return PropertiesConfigService.isDevMode();
    }

    public boolean isDemoMode() {
        return PropertiesConfigService.isDemoMode();
    }

    public String getSystemTitle() {
        return dynamicConfigService.getSystemTitle();
    }

    public boolean isSignupEnabled() {
        return !dynamicConfigService.isSignupDisabled();
    }

    public boolean isCasSupport() {
        return casAuthenticationEntryPoint != null;
    }

    public boolean isMailServiceEnabled() {
        return mailService.isEnabled();
    }

    @Autowired(required = false)
    private CasAuthenticationEntryPoint casAuthenticationEntryPoint;

    public String getCasRedirectUrl() {
        HttpServletRequest request = ServletActionContext.getRequest();
        final StringBuilder buffer = new StringBuilder();
        buffer.append(request.isSecure() ? "https://" : "http://");
        buffer.append(request.getServerName());
        buffer.append(request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
        buffer.append(request.getContextPath());
        buffer.append("/j_spring_cas_security_check");

        final String urlEncodedService = ServletActionContext.getResponse().encodeURL(buffer.toString());
        final String redirectUrl = CommonUtils.constructRedirectUrl(casAuthenticationEntryPoint.getLoginUrl(),
                casAuthenticationEntryPoint.getServiceProperties().getServiceParameter(), urlEncodedService,
                casAuthenticationEntryPoint.getServiceProperties().isSendRenew(), false);
        return redirectUrl;
    }

    @MetaData("�����һ�����")
    public HttpHeaders forget() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String uid = getRequiredParameter("uid");
        String jCaptcha = getRequiredParameter("j_captcha");
        if (!ImageCaptchaServlet.validateResponse(request, jCaptcha)) {
            setModel(OperationResult.buildFailureResult("��֤�벻��ȷ������������"));
        } else {
            User user = null;
            if (uid.indexOf("@") > -1) {
                user = userService.findByProperty("email", uid);
            }
            if (user == null) {
                user = userService.findBySigninid(uid);
            }
            if (user == null) {
                setModel(OperationResult.buildFailureResult("δ�ҵ�ƥ���˺���Ϣ������ϵ����Ա����"));
            } else {
                String email = user.getEmail();
                if (StringUtils.isBlank(email)) {
                    setModel(OperationResult.buildFailureResult("��ǰ�˺�δ�趨ע�����䣬����ϵ����Ա������������ٽ��д˲���"));
                } else {
                    userService.requestResetPassword(user);
                    setModel(OperationResult.buildSuccessResult("�һ�����������ɹ�", user.getEmail()));
                }
            }
        }
        return buildDefaultHttpHeaders();
    }

    @MetaData("��������")
    public HttpHeaders resetpwd() {
        String email = getRequiredParameter("email");
        String code = getRequiredParameter("code");
        String password = getRequiredParameter("password");
        User user = userService.findByProperty("email", email);
        if (user == null) {
            setModel(OperationResult.buildFailureResult("δ�ҵ�ƥ���˺���Ϣ������ϵ����Ա����"));
            return buildDefaultHttpHeaders();
        }
        if (!code.equals(user.getRandomCode())) {
            setModel(OperationResult.buildFailureResult("У�����ѹ��ڣ��������һ��������"));
            return buildDefaultHttpHeaders();
        }

        userService.resetPassword(user, password);
        setModel(OperationResult.buildSuccessResult("�������óɹ�"));

        return buildDefaultHttpHeaders();
    }

    @MetaData("�Ự����")
    public HttpHeaders expired() {
        return buildDefaultHttpHeaders("expired");
    }
}
