package com.etrade.framework.core.auth.security;

import com.etrade.framework.core.auth.dao.UserDao;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.security.AuthContextHolder;

/**
 * 基于Spring Security获取当前登录用户对象的帮助类
 *
 */
public class AuthUserHolder {

    /**
     * 获取当前登录用户对象
     */
    public static User getLogonUser() {
        UserDao userDao = SpringContextHolder.getBean(UserDao.class);
        return userDao.findByUid(AuthContextHolder.getAuthUserDetails().getUid());
    }

    /**
     * 获取当前登录用户所属部门对象
     */
    public static Department getLogonUserDepartment() {
        User user = getLogonUser();
        if (user == null) {
            return null;
        }
        return user.getDepartment();
    }
}
