/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.security;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * ��ThreadLocal��ʽʵ��Web�˵�¼��Ϣ���ݵ�ҵ���Ĵ�ȡ
 */
public class AuthContextHolder {

    public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";

    public static final String DEFAULT_UNKNOWN_PIN = "N/A";

    /**
     * ��ȡ�û�Ψһ��¼��ʶ
     */
    public static String getAuthUserPin() {
        String pin = DEFAULT_UNKNOWN_PIN;
        AuthUserDetails authUserDetails = getAuthUserDetails();
        if (authUserDetails != null && authUserDetails.getUsername() != null) {
            pin = authUserDetails.getUsername();
        }
        return pin;
    }

    /**
     * ����Spring Security��ȡ�û���֤��Ϣ
     */
    public static AuthUserDetails getAuthUserDetails() {
        AuthUserDetails userDetails = null;
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthUserDetails) {
            userDetails = (AuthUserDetails) principal;
        }
        Assert.notNull(userDetails);
        return userDetails;
    }

    public static boolean isAdminUser() {
        return getAuthUserDetails().isAdminUser();
    }

    /**
     * ��ȡ�û�ACL CODE
     */
    public static String getAclCode() {
        return getAuthUserDetails().getAclCode();
    }

    /**
     * ��ȡ�û�ACL CODE
     */
    public static String getAclCodePrefix() {
        String aclCode = getAclCode();
        if (StringUtils.isBlank(aclCode)) {
            return "";
        }
        Collection<String> aclCodePrefixs = getAclCodePrefixs();
        if (CollectionUtils.isEmpty(aclCodePrefixs)) {
            return "";
        }
        for (String aclCodePrefix : aclCodePrefixs) {
            if (aclCode.startsWith(aclCodePrefix)) {
                return aclCodePrefix;
            }
        }
        throw new IllegalStateException("ACLǰ׺�����쳣");
    }

    public static Collection<String> getAclCodePrefixs() {
        return getAuthUserDetails().getAclCodePrefixs();
    }
}
