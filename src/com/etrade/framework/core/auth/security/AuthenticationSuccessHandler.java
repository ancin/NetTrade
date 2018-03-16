package com.etrade.framework.core.auth.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.security.AuthUserDetails;
import com.etrade.framework.core.util.IPAddrFetcher;

/**
 * Spring Security��֤�ɹ�������ʷ��¼
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    private UserService  userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException,
                                                                      ServletException {
        try {

            AuthUserDetails authUserDetails = AuthContextHolder.getAuthUserDetails();

            logger.debug("Removing " + AuthContextHolder.SPRING_SECURITY_LAST_USERNAME_KEY
                         + " from session...");
            HttpSession session = request.getSession(false);
            if (session != null) {
                request.getSession().removeAttribute(
                    AuthContextHolder.SPRING_SECURITY_LAST_USERNAME_KEY);
            }

            String username = authUserDetails.getUsername();
            User user = userService.findBySigninid(username);
            logger.debug("Inserting AuthenticationSuccess history record for: {}", username);
            //д������¼��Ϣ
            UserLogonLog userLogonLog = new UserLogonLog();
            userLogonLog.setAuthenticationFailure(false);
            userLogonLog.setLogonTime(new Date());
            userLogonLog.setRemoteAddr(request.getRemoteAddr());
            userLogonLog.setRemoteHost(request.getRemoteHost());
            userLogonLog.setRemotePort(request.getRemotePort());
            userLogonLog.setLocalAddr(request.getLocalAddr());
            userLogonLog.setLocalName(request.getLocalName());
            userLogonLog.setLocalPort(request.getLocalPort());
            userLogonLog.setServerIP(IPAddrFetcher.getGuessUniqueIP());
            userLogonLog.setHttpSessionId(request.getSession().getId());
            userLogonLog.setUserAgent(request.getHeader("User-Agent"));
            userLogonLog.setXforwardFor(IPAddrFetcher.getRemoteIpAddress(request));
            userLogonLog.setUserid(authUserDetails.getUid());
            userLogonLog.setUsername(username);
            if (user != null) {
                user.setLogonTimes(user.getLogonTimes() == null ? 1 : user.getLogonTimes() + 1);
                userLogonLog.setLogonTimes(user.getLogonTimes());
            }
            userService.userLogonLog(userLogonLog);

            if (user != null) {
                user.setLastLogonIP(userLogonLog.getRemoteAddr());
                user.setLastLogonHost(userLogonLog.getRemoteHost());
                user.setLastLogonTime(userLogonLog.getLogonTime());

                //����ʧ�ܴ�������
                user.setLastLogonFailureTime(null);
                user.setLogonFailureTimes(0);
                userService.save(user);
            }
        } catch (Exception e) {
            logger.error("error.spring.security.insert.logon.hist", e);
        }

        //Hack RememberMeʱ��������������֤֮ǰ������
        //�ο���https://jira.springsource.org/browse/SEC-1991
        String url = request.getServletPath();
        if (url != null && url.indexOf("j_spring") == -1) {
            HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
            requestCache.saveRequest(request, response);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
