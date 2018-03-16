package com.etrade.framework.core.auth.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.IdentityService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.etrade.framework.core.auth.dao.PrivilegeDao;
import com.etrade.framework.core.auth.dao.RoleDao;
import com.etrade.framework.core.auth.dao.UserDao;
import com.etrade.framework.core.auth.dao.UserLogonLogDao;
import com.etrade.framework.core.auth.dao.UserOauthDao;
import com.etrade.framework.core.auth.dao.UserR2RoleDao;
import com.etrade.framework.core.auth.entity.Privilege;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.entity.UserLogonLog;
import com.etrade.framework.core.auth.entity.UserOauth;
import com.etrade.framework.core.auth.entity.UserR2Role;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.ctx.FreemarkerService;
import com.etrade.framework.core.ctx.MailService;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.exception.ServiceException;
import com.etrade.framework.core.security.AclService;
import com.etrade.framework.core.security.AuthUserDetails;
import com.etrade.framework.core.service.BaseService;
import com.google.common.collect.Maps;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class UserService extends BaseService<User, Long> {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserOauthDao userOauthDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserR2RoleDao userR2RoleDao;

    @Autowired
    private UserLogonLogDao userLogonLogDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired(required = false)
    private FreemarkerService freemarkerService;

    @Autowired(required = false)
    private AclService aclService;
    @Autowired(required = false)
    private IdentityService identityService;

    @Override
    protected BaseDao<User, Long> getEntityDao() {
        return userDao;
    }

    @Override
    protected void preInsert(User entity) {
        super.preInsert(entity);
        String email = entity.getEmail();
        if (StringUtils.isNotBlank(email) && findByProperty("email", email) != null) {
            throw new ServiceException("�ʼ� [" + email + "] �ѱ�ע��ռ��");
        }
        if (aclService != null) {
            entity.setAclType(aclService.aclCodeToType(entity.getAclCode()));
        }
        entity.setSignupTime(new Date());
    }

    @Override
    protected void preUpdate(User entity) {
        if (aclService != null) {
            entity.setAclType(aclService.aclCodeToType(entity.getAclCode()));
        }
        super.preUpdate(entity);
    }

    public User findBySigninid(String signinid) {
        return findByProperty("signinid", signinid);
    }

    @Override
    public User save(User user) {
        if (user.isNew()) {
            user.setUserPin(user.getSigninid());
            user.setUid(RandomStringUtils.randomNumeric(10));
        }
        super.save(user);

        // ��������Activiti���û�Ȩ�޿�������
        cascadeActivitiIndentityData(user);

        return user;
    }

    /**
     * �û�ע��
     * 
     * @param rawPassword
     *            ԭʼ����
     * @param user
     *            �û����ݶ���
     * @return
     */
    public User save(User user, String rawPassword) {
        if (StringUtils.isNotBlank(rawPassword)) {
            // �����޸ĺ�����������ʱ��Ϊ6����
            user.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
            user.setPassword(encodeUserPasswd(user, rawPassword));
        }
        return save(user);
    }

    public boolean validatePassword(String signinid, String rawPassword) {
        User user = findBySigninid(signinid);
        //�û��˺Ų����ڣ�ֱ�ӷ���������֤ʧ��
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(encodeUserPasswd(user, rawPassword));
    }

    public String encodeUserPasswd(User user, String rawPassword) {
        return passwordEncoder.encodePassword(rawPassword, user.getUid());
    }

    public void updateRelatedRoleR2s(Long id, String... roleIds) {
        updateRelatedR2s(id, roleIds, "userR2Roles", "role");
        // ��������Activiti���û�Ȩ�޿�������
        cascadeActivitiIndentityData(userDao.findOne(id), roleIds);
    }

    private void cascadeActivitiIndentityData(User user, String... roleIds) {
        if (identityService != null) {
            String userId = user.getSigninid();
            org.activiti.engine.identity.User identityUser = identityService.createUserQuery().userId(userId).singleResult();
            List<org.activiti.engine.identity.Group> activitiGroups = null;
            if (identityUser != null) {
                // ������Ϣ
                identityUser.setFirstName(user.getNick());
                identityUser.setLastName("");
                identityUser.setPassword(user.getPassword());
                identityUser.setEmail(user.getEmail());
                identityService.saveUser(identityUser);

                // ��ɾ�����е�membership
                activitiGroups = identityService.createGroupQuery().groupMember(userId).list();
                for (org.activiti.engine.identity.Group group : activitiGroups) {
                    if (group.getType().equals("ACL_CODE")) {
                        identityService.deleteMembership(userId, group.getId());
                    }
                }
            } else {
                // �����û�����
                identityUser = identityService.newUser(user.getSigninid());
                identityUser.setFirstName(user.getNick());
                identityUser.setLastName("");
                identityUser.setPassword(user.getPassword());
                identityUser.setEmail(user.getEmail());
                identityService.saveUser(identityUser);
            }

            // ���membership
            String aclCode = user.getAclCode();
            if (StringUtils.isNotBlank(aclCode)) {
                String groupId = aclCode;
                org.activiti.engine.identity.Group identityGroup = identityService.createGroupQuery().groupId(groupId).singleResult();
                if (identityGroup == null) {
                    identityGroup = identityService.newGroup(groupId);
                    identityGroup.setName("��������");
                    identityGroup.setType("ACL_CODE");
                    identityService.saveGroup(identityGroup);
                }
                identityService.createMembership(userId, groupId);
            }

            // ���role����membership
            if (roleIds != null) {
                // ��ɾ�����н�ɫ����
                if (activitiGroups != null) {
                    for (org.activiti.engine.identity.Group group : activitiGroups) {
                        if (group.getType().equals(Role.class.getSimpleName())) {
                            identityService.deleteMembership(userId, group.getId());
                        }
                    }
                }
                for (String roleId : roleIds) {
                    Role role = roleDao.findOne(roleId);
                    String groupId = role.getCode();
                    org.activiti.engine.identity.Group identityGroup = identityService.createGroupQuery().groupId(groupId).singleResult();
                    if (identityGroup == null) {
                        identityGroup = identityService.newGroup(groupId);
                        identityGroup.setName(role.getTitle());
                        identityGroup.setType(Role.class.getSimpleName());
                        identityService.saveGroup(identityGroup);
                    }
                    identityService.createMembership(userId, groupId);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Privilege> findRelatedPrivilegesForUser(User user) {
        return privilegeDao.findPrivilegesForUser(user);
    }

    @Async
    public void userLogonLog(UserLogonLog userLogonLog) {
        if (userLogonLogDao.findByHttpSessionId(userLogonLog.getHttpSessionId()) != null) {
            return;
        }
        userLogonLogDao.save(userLogonLog);
    }

    public User findByOauthUser(String username) {
        UserOauth userOauth = userOauthDao.findByUsername(username);
        if (userOauth == null) {
            return null;
        } else {
            return userOauth.getUser();
        }
    }

    public Long findUserCount() {
        return userDao.findUserCount();
    }

    /**
     * �����û�Ȩ�����ݶ���
     * 
     * @param username
     * @return
     */
    public UserDetails loadUserDetails(String username) {
        logger.debug("Loading user details for: {}", username);

        User user = null;
        // ����ʼ���¼֧��
        if (username.indexOf("@") > -1) {
            user = findByProperty("email", username);
        }
        if (user == null) {
            user = findByProperty("signinid", username);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        boolean enabled = user.getEnabled() == null ? true : user.getEnabled();
        boolean accountNonLocked = user.getAccountNonLocked() == null ? true : user.getAccountNonLocked();
        Date now = new Date();
        boolean credentialsNonExpired = user.getCredentialsExpireTime() == null ? true : user.getCredentialsExpireTime().after(now);
        boolean accountNonExpired = user.getAccountExpireTime() == null ? true : user.getAccountExpireTime().after(now);

        if (!enabled) {
            throw new DisabledException("User '" + username + "' disabled");
        }
        if (!credentialsNonExpired) {
            throw new CredentialsExpiredException("User '" + username + "' credentials expired");
        }
        if (!accountNonLocked) {
            throw new LockedException("User '" + username + "' account locked");
        }
        if (!accountNonExpired) {
            throw new AccountExpiredException("User '" + username + "' account expired");
        }

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();
        Iterable<UserR2Role> r2s = userR2RoleDao.findEnabledRolesForUser(user);
        for (UserR2Role userR2Role : r2s) {
            String roleCode = userR2Role.getRole().getCode();
            dbAuthsSet.add(new SimpleGrantedAuthority(roleCode));
        }
        dbAuthsSet.add(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUSLY_CODE));

        if (logger.isDebugEnabled()) {
            logger.debug("User role list for: {}", username);
            for (GrantedAuthority ga : dbAuthsSet) {
                logger.debug(" - " + ga.getAuthority());
            }
        }

        AuthUserDetails authUserDetails = new AuthUserDetails(username, user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, dbAuthsSet);
        authUserDetails.setUid(user.getUid());
        authUserDetails.setAclCode(user.getAclCode());
        authUserDetails.setAclType(user.getAclType());
        authUserDetails.setEmail(user.getEmail());

        if (aclService != null) {
            authUserDetails.setAclCodePrefixs(aclService.getStatAclCodePrefixs(user.getAclCode()));
        }

        // �����û�ӵ�е�Ȩ�޴��뼯��
        Set<String> privilegeCodeSet = new HashSet<String>();
        List<Privilege> privileges = privilegeDao.findPrivilegesForUser(user);
        if (logger.isDebugEnabled()) {
            logger.debug("User privilege list for: {}", username);
            for (Privilege privilege : privileges) {
                logger.debug(" - {} : {}", privilege.getCode(), privilege.getUrl());
            }
        }
        for (Privilege privilege : privileges) {
            privilegeCodeSet.add(privilege.getCode().trim());
        }
        authUserDetails.setPrivilegeCodes(privilegeCodeSet);

        return authUserDetails;
    }

    public void requestResetPassword(User user) {
        String email = user.getEmail();
        Assert.isTrue(StringUtils.isNotBlank(email), "User email required");
        String suject = dynamicConfigService.getString("cfg.user.reset.pwd.notify.email.title",
            "�������������ʼ�");
        user.setRandomCode(UUID.randomUUID().toString());
        userDao.save(user);

        HttpServletRequest request = ServletActionContext.getRequest();
        int serverPort = request.getServerPort();
        // Reconstruct original requesting URL
        StringBuffer url = new StringBuffer();
        url.append(request.getScheme()).append("://").append(request.getServerName());
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        String contextPath = request.getContextPath();
        if (!"/".equals(contextPath)) {
            url.append(contextPath);
        }
        url.append("/pub/signin?email=" + email + "&code=" + user.getRandomCode());

        if (freemarkerService != null) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("user", user);
            params.put("resetPasswordLink", url.toString());
            String contents = freemarkerService.processTemplateByFileName("PASSWORD_RESET_NOTIFY_EMAIL", params);
            mailService.sendHtmlMail(suject, contents, true, email);
        } else {
            mailService.sendHtmlMail(suject, url.toString(), true, email);
        }
    }

    public void resetPassword(User user, String rawPassword) {
        user.setRandomCode(null);
        save(user, rawPassword);
    }

    public List<User> findByAclCode(String aclCode) {
        return userDao.findByAclCode(aclCode);
    }

    /**
     * ����Ӧ�õ��û���������Activiti������������û���������
     */
    public void resetActivitiIndentityData() {
        Iterable<User> users = userDao.findAll();
        for (User user : users) {
            List<UserR2Role> userR2Roles = user.getUserR2Roles();
            String[] roleIds = new String[userR2Roles.size()];
            for (int i = 0; i < roleIds.length; i++) {
                roleIds[i] = userR2Roles.get(i).getRole().getId();
            }
            cascadeActivitiIndentityData(user, roleIds);
        }
    }
}
