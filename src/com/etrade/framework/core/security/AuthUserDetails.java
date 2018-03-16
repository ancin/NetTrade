/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * ��չSpring Security��UserDetails����׷��email��Ȩ�޴��뼯�ϵ���Ϣ
 */
public class AuthUserDetails implements UserDetails {

    private static final long serialVersionUID = 8346793124666695534L;

    public static final String ROLE_ADMIN_CODE = "ROLE_ADMIN";

    /** ȫ��Ψһ���û���ʶ */
    private String uid;

    /** The password. */
    private String password;

    /** The username. */
    private String username;

    private String email;

    /** The authorities. */
    private Set<GrantedAuthority> authorities;

    /** The account non expired. */
    private boolean accountNonExpired = true;

    /** The account non locked. */
    private boolean accountNonLocked = true;

    /** The credentials non expired. */
    private boolean credentialsNonExpired = true;

    /** The enabled. */
    private boolean enabled;

    /** The privilege codes. */
    private Collection<String> privilegeCodes;

    /** ���ݷ��ʿ��ƴ��� */
    private String aclCode;

    /** ���ݷ��ʿ�������(���ִ�Ȩ�ް�������С) */
    private String aclType;

    /** ��չ������������CAS Oauth��֤�����û���Ϣ */
    private Map<String, Object> attributes;

    /**
     * ����һ����һACL Code��������Է��ʵ�ACL Codeǰ׺����
     * ���û�ACL CodeΪ120000������ҵ����������ǰ׺���Ͽ�ת��12, AA12,BB12��
     * @see AclService#getStatAclCodePrefixs(String)
     */
    private Collection<String> aclCodePrefixs;

    /**
     * Gets the password.
     * 
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the authorities.
     * 
     * @return the authorities
     */
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Sets the authorities.
     * 
     * @param authorities
     *            the authorities to set
     */
    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * Checks if is account non expired.
     * 
     * @return the accountNonExpired
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * Sets the account non expired.
     * 
     * @param accountNonExpired
     *            the accountNonExpired to set
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * Checks if is account non locked.
     * 
     * @return the accountNonLocked
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * Sets the account non locked.
     * 
     * @param accountNonLocked
     *            the accountNonLocked to set
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * Checks if is credentials non expired.
     * 
     * @return the credentialsNonExpired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * Sets the credentials non expired.
     * 
     * @param credentialsNonExpired
     *            the credentialsNonExpired to set
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * Checks if is enabled.
     * 
     * @return the enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the privilege codes.
     * 
     * @return the privilegeCodes
     */
    public Collection<String> getPrivilegeCodes() {
        return privilegeCodes;
    }

    /**
     * Sets the privilege codes.
     * 
     * @param privilegeCodes
     *            the privilegeCodes to set
     */
    public void setPrivilegeCodes(Collection<String> privilegeCodes) {
        this.privilegeCodes = privilegeCodes;
    }

    /**
     * Construct the <code>User</code> with the details required by.
     * 
     * @param username
     *            the username presented to the
     *            <code>DaoAuthenticationProvider</code>
     * @param password
     *            the password that should be presented to the
     *            <code>DaoAuthenticationProvider</code>
     * @param enabled
     *            set to <code>true</code> if the user is enabled
     * @param accountNonExpired
     *            set to <code>true</code> if the account has not expired
     * @param credentialsNonExpired
     *            set to <code>true</code> if the credentials have not expired
     * @param accountNonLocked
     *            set to <code>true</code> if the account is not locked
     * @param authorities
     *            the authorities that should be granted to the caller if they
     *            presented the correct username and password and the user is
     *            enabled
     * @throws IllegalArgumentException
     *             if a <code>null</code> value was passed either as a parameter
     *             or as an element in the <code>GrantedAuthority[]</code> array
     *             {@link org.springframework.security.providers.dao.DaoAuthenticationProvider}
     *             .
     */
    public AuthUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Set<GrantedAuthority> authorities)
            throws IllegalArgumentException {
        if (((username == null) || "".equals(username)) || (password == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
    }

    public AuthUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Set<GrantedAuthority> authorities,
            Map<String, Object> attributes) throws IllegalArgumentException {
        this(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.attributes = attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("username:" + username);
        sb.append("; ");
        boolean first = true;
        if (authorities != null && authorities.size() > 0) {
            sb.append("GrantedAuthority: ");
            for (GrantedAuthority grantedAuthority : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(grantedAuthority.toString());
            }
        } else {
            sb.append("Not GrantedAuthority");
        }
        sb.append("; ");
        if (privilegeCodes != null && !privilegeCodes.isEmpty()) {
            sb.append("Granted Privilege Codes: ");

            first = true;
            for (String code : privilegeCodes) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(code);
            }
        } else {
            sb.append("Not granted any privilege codes");
        }
        return sb.toString();
    }

    public String getUsernameDisplay() {
        return username;
    }

    public String getAclCode() {
        return aclCode;
    }

    public void setAclCode(String aclCode) {
        this.aclCode = aclCode;
    }

    public String getAclType() {
        return aclType;
    }

    public void setAclType(String aclType) {
        this.aclType = aclType;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Collection<String> getAclCodePrefixs() {
        return aclCodePrefixs;
    }

    public void setAclCodePrefixs(Collection<String> aclCodePrefixs) {
        this.aclCodePrefixs = aclCodePrefixs;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.getUsername()).toHashCode();
    }

    @Override
    public boolean equals(Object user) {
        if (this == user) {
            return true;
        }
        if (!(user instanceof AuthUserDetails)) {
            return false;
        }
        return new EqualsBuilder().append(getUsername(), ((AuthUserDetails) user).getUsername()).isEquals();
    }

    public boolean isAdminUser() {
        for (GrantedAuthority grantedAuthority : authorities) {
            if (ROLE_ADMIN_CODE.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
