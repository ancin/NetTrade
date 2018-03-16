package com.etrade.framework.core.auth.security.cas;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import com.etrade.framework.core.auth.service.UserService;

/**
 * ����CAS����,û�������ж�,ֱ�ӻ�ȡ�û���Ϣ��ʵ�ֶ�����û���Ϣ��ѯ�Լ��û�����Ȩ�޴��뼯�ϲ�ѯ����
 */
public class CASAuthUserDetailsService extends AbstractCasAssertionUserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(CASAuthUserDetailsService.class);

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserDetails(Assertion assertion) {
        String username = assertion.getPrincipal().getName();
        logger.info("CAS assertion principal name: {}", username);
        if (logger.isDebugEnabled()) {
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            logger.debug("Principal Attributes:");
            for (Map.Entry<String, Object> me : attributes.entrySet()) {
                logger.debug(" - {}={}", me.getKey(), me.getValue());
            }
        }
        if (username.indexOf("#") > -1) {
            username = StringUtils.substringAfterLast(username, "#");
            logger.info("CAS assertion principal name converted to loginid: {}", username);
        }

        return userService.loadUserDetails(username);
    }

}
