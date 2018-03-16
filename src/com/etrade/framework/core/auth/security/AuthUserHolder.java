package com.etrade.framework.core.auth.security;

import com.etrade.framework.core.auth.dao.UserDao;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.security.AuthContextHolder;

/**
 * ����Spring Security��ȡ��ǰ��¼�û�����İ�����
 *
 */
public class AuthUserHolder {

    /**
     * ��ȡ��ǰ��¼�û�����
     */
    public static User getLogonUser() {
        UserDao userDao = SpringContextHolder.getBean(UserDao.class);
        return userDao.findByUid(AuthContextHolder.getAuthUserDetails().getUid());
    }

    /**
     * ��ȡ��ǰ��¼�û��������Ŷ���
     */
    public static Department getLogonUserDepartment() {
        User user = getLogonUser();
        if (user == null) {
            return null;
        }
        return user.getDepartment();
    }
}
