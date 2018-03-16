package com.etrade.framework.core.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.dao.PrivilegeDao;
import com.etrade.framework.core.auth.dao.RoleDao;
import com.etrade.framework.core.auth.dao.RoleR2PrivilegeDao;
import com.etrade.framework.core.auth.dao.UserR2RoleDao;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.entity.UserR2Role;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.google.common.collect.Lists;

@Service
@Transactional
public class RoleService extends BaseService<Role, String> {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserR2RoleDao userR2RoleDao;

    @Autowired
    private RoleR2PrivilegeDao roleR2PrivilegeDao;

    @Override
    protected BaseDao<Role, String> getEntityDao() {
        return roleDao;
    }

    @Transactional(readOnly = true)
    public List<Role> findAllCached() {
        return roleDao.findAllCached();
    }

    @Transactional(readOnly = true)
    public List<Role> findR2RolesForUser(User user) {
        List<Role> roles = Lists.newArrayList();
        Iterable<UserR2Role> r2s = userR2RoleDao.findEnabledRolesForUser(user);
        for (UserR2Role r2 : r2s) {
            roles.add(r2.getRole());
        }
        return roles;
    }

    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void updateRelatedPrivilegeR2s(String roleId, String[] r2Ids) {
        updateRelatedR2s(roleId, r2Ids, "roleR2Privileges", "privilege");
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public Role save(Role entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void delete(Role entity) {
        super.delete(entity);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public List<Role> save(Iterable<Role> entities) {
        return super.save(entities);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void delete(Iterable<Role> entities) {
        super.delete(entities);
    }
}
