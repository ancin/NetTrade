package com.etrade.framework.core.auth.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.util.IPAddrFetcher;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

    private int maxAuthenticationFailureTimes = 20;

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMaxAuthenticationFailureTimes(int maxAuthenticationFailureTimes) {
        this.maxAuthenticationFailureTimes = maxAuthenticationFailureTimes;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String username = null;
        if (exception != null && exception.getAuthentication() != null) {
            username = exception.getAuthentication().getName();
        }
        if (StringUtils.isNotBlank(username)) {
            User user = userService.findBySigninid(username);
            if (user != null) {

                logger.debug("Inserting AuthenticationFailure history record for: {}", username);

                //�ۼ�ʧ�ܴ���
                user.setLastLogonFailureTime(new Date());
                user.setLogonFailureTimes(user.getLogonFailureTimes() == null ? 1 : user.getLogonFailureTimes() + 1);

                //д������¼��Ϣ
                UserLogonLog userLogonLog = new UserLogonLog();
                userLogonLog.setAuthenticationFailure(true);
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
                userLogonLog.setXforwardFor(request.getHeader("X-Forward-For"));
                userLogonLog.setUserid(user.getUid());
                userLogonLog.setUsername(username);
                userLogonLog.setLogonTimes(user.getLogonFailureTimes());
                userService.userLogonLog(userLogonLog);

                //����������֤ʧ�ܵĴ������ƣ������ʺţ����ɹ���Ա����
                if (user.getLogonFailureTimes() > maxAuthenticationFailureTimes) {
                    user.setAccountNonLocked(false);
                    logger.warn("Account[{}] has been locked as reached maxAuthenticationFailureTimes={}", username,
                            maxAuthenticationFailureTimes);
                }
                userService.save(user);
            }
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
