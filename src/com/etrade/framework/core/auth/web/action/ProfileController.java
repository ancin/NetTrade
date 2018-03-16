package com.etrade.framework.core.auth.web.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.security.AuthUserHolder;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.security.AuthUserDetails;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData(value = "��������")
public class ProfileController extends SimpleController {

    @Autowired
    private UserService userService;

    @SecurityControlIgnore
    @MetaData(value = "�����޸���ʾ")
    public HttpHeaders passwd() {
        return new DefaultHttpHeaders("passwd").disableCaching();
    }

    @SecurityControlIgnore
    @MetaData(value = "�����޸Ĵ���")
    public HttpHeaders doPasswd() {
        Validation.notDemoMode();
        AuthUserDetails authUserDetails = AuthContextHolder.getAuthUserDetails();
        Assert.notNull(authUserDetails);
        HttpServletRequest request = ServletActionContext.getRequest();
        String oldpasswd = request.getParameter("oldpasswd");
        String newpasswd = request.getParameter("newpasswd");
        Assert.isTrue(StringUtils.isNotBlank(oldpasswd));
        Assert.isTrue(StringUtils.isNotBlank(newpasswd));

        User user = AuthUserHolder.getLogonUser();
        String encodedPasswd = userService.encodeUserPasswd(user, oldpasswd);
        if (!encodedPasswd.equals(user.getPassword())) {
            setModel(OperationResult.buildFailureResult("ԭ���벻��ȷ,����������"));
        } else {
            userService.save(user, newpasswd);
            setModel(OperationResult.buildSuccessResult("�����޸ĳɹ�,�����´ε�¼ʹ��������"));
        }
        return new DefaultHttpHeaders().disableCaching();
    }
}
