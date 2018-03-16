package com.etrade.framework.core.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.etrade.framework.core.auth.security.ExtSecurityInterceptor;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.security.AuthUserDetails;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.sys.service.MenuService;
import com.etrade.framework.core.sys.vo.NavMenuVO;
import com.etrade.framework.core.web.view.OperationResult;

/**
 * 全局布局处理
 */
public class LayoutController extends SimpleController {

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    public String getSystemTitle() {
        return dynamicConfigService.getSystemTitle();
    }

    public AuthUserDetails getAuthUserDetails() {
        return AuthContextHolder.getAuthUserDetails();
    }

    public String getBaiduMapAppid() {
        return dynamicConfigService.getString("baidu.map.appid");
    }

    @Override
    public HttpHeaders index() {
        return start();
    }

    public HttpHeaders start() {
        HttpServletRequest request = ServletActionContext.getRequest();
        List<NavMenuVO> menus = menuService.authUserMenu(AuthContextHolder.getAuthUserDetails().getAuthorities(),
                request.getContextPath());
        request.setAttribute("rootMenus", menus);
        return buildDefaultHttpHeaders("start");
    }

    public HttpHeaders dashboard() {
        return buildDefaultHttpHeaders("dashboard");
    }

    public HttpHeaders exception() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String type = request.getParameter("type");
        if (type != null) {
            throw new IllegalArgumentException("Mock Exception");
        }
        return buildDefaultHttpHeaders("mock");
    }

    public HttpHeaders lock() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        Assert.notNull(AuthContextHolder.getAuthUserPin());
        session.setAttribute(ExtSecurityInterceptor.SESSION_KEY_LOCKED, true);
        setModel(OperationResult.buildSuccessResult("会话已锁定"));
        return buildDefaultHttpHeaders();
    }

    public HttpHeaders unlock() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        Assert.isTrue(session.getAttribute(ExtSecurityInterceptor.SESSION_KEY_LOCKED) != null, "Session unlocked");
        String password = request.getParameter("password");
        Validation.isTrue(
            userService.validatePassword(AuthContextHolder.getAuthUserPin(), password), "密码不正确");
        session.removeAttribute(ExtSecurityInterceptor.SESSION_KEY_LOCKED);
        setModel(OperationResult.buildSuccessResult("会话已解锁"));
        return buildDefaultHttpHeaders();
    }
}
