package com.etrade.framework.core.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.etrade.framework.core.auth.service.UserService;

/**
 * ��չ��׼��JDBCȨ�޷��ʽӿڣ�ʵ�ֶ�����û���Ϣ��ѯ�Լ��û�����Ȩ�޴��뼯�ϲ�ѯ����
 */
public class AuthUserDetailsService implements UserDetailsService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.loadUserDetails(username);
    }
}
