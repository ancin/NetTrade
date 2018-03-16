package com.etrade.framework.core.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.framework.core.auth.dao.PrivilegeDao;
import com.etrade.framework.core.auth.dao.RoleDao;
import com.etrade.framework.core.auth.dao.RoleR2PrivilegeDao;
import com.etrade.framework.core.auth.entity.Privilege;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.auth.entity.RoleR2Privilege;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.rpt.dao.ReportDefDao;
import com.etrade.framework.core.rpt.entity.ReportDef;
import com.etrade.framework.core.rpt.entity.ReportDefR2Role;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.filter.PostStrutsPrepareAndExecuteFilter;

@Service
@Transactional
public class PrivilegeService extends BaseService<Privilege, String> {

    private final Logger logger = LoggerFactory.getLogger(PrivilegeService.class);

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private RoleR2PrivilegeDao roleR2PrivilegeDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ReportDefDao reportDefDao;

    /***
     * TODOĬ����high
     */
    @Value("high")
    private String authControlLevel;

    @Override
    protected BaseDao<Privilege, String> getEntityDao() {
        return privilegeDao;
    }

    @Transactional(readOnly = true)
    public List<Privilege> findAllCached() {
        return privilegeDao.findAllCached();
    }

    @Transactional(readOnly = true)
    public List<String> findDistinctCategories() {
        return privilegeDao.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public Page<Privilege> findUnRelatedPrivilegesForRole(final String roleId, final GroupPropertyFilter groupFilter,
            Pageable pageable) {
        Specification<Privilege> specification = new Specification<Privilege>() {
            @Override
            public Predicate toPredicate(Root<Privilege> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Predicate predicate = buildPredicatesFromFilters(groupFilter, root, query, builder);
                Subquery<RoleR2Privilege> sq = query.subquery(RoleR2Privilege.class);
                Root<RoleR2Privilege> r2 = sq.from(RoleR2Privilege.class);
                sq.where(builder.equal(r2.get("privilege"), root), builder.equal(r2.get("role").get("id"), roleId))
                        .select(r2);
                return builder.and(predicate, builder.not(builder.exists(sq)));
            }
        };
        return privilegeDao.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public List<RoleR2Privilege> findRelatedRoleR2PrivilegesForPrivilege(String privilegeId) {
        return roleR2PrivilegeDao.findByPrivilege_Id(privilegeId);
    }

    //����������Դ��Ȩ�޵Ĺ�ϵ   
    @Transactional(readOnly = true)
    @Cacheable("SpringSecurityCache")
    public Map<String, Collection<ConfigAttribute>> loadResourceDefine() {
        logger.debug("Prepare to load Spring Security Resource Mapping...");

        Map<String, Collection<ConfigAttribute>> resourceMap = new LinkedHashMap<String, Collection<ConfigAttribute>>();

        //��������SecurityControllIgnore��ǵ�Ȩ��Ϊ��¼�ɷ���, ���ݼ�����ԴΪStruts2ע������securityControllIgnoreUrls
        for (String url : PostStrutsPrepareAndExecuteFilter.securityControlIgnoreUrls) {
            addURL2Role(resourceMap, url, Role.ROLE_ANONYMOUSLY_CODE);
        }

        //���ý�ɫ����Ȩ��
        Iterable<RoleR2Privilege> r2s = roleR2PrivilegeDao.findEnabledExcludeRole(Role.ROLE_ADMIN_CODE);
        for (RoleR2Privilege r2 : r2s) {
            if (StringUtils.isNotBlank(r2.getPrivilege().getUrl())) {
                addURL2Role(resourceMap, r2.getPrivilege().getUrl(), r2.getRole().getCode());
            }
        }

        Iterable<Privilege> privileges = privilegeDao.findByDisabled(false);
        for (Privilege privilege : privileges) {
            if (StringUtils.isNotBlank(privilege.getUrl())) {
                //ROLE_ADMIN��ɫĬ�ϸ�������Ȩ��
                addURL2Role(resourceMap, privilege.getUrl(), Role.ROLE_ADMIN_CODE);
            }
        }

        //����URLȨ�޿���
        List<ReportDef> reportDefs = reportDefDao.findDisplayItems();
        for (ReportDef reportDef : reportDefs) {
            String url = reportDef.getReportAccessUrl();
            //ROLE_ADMIN��ɫĬ�ϸ�������Ȩ��
            addURL2Role(resourceMap, url, Role.ROLE_ADMIN_CODE);
            //ǿ���ܿ�
            addURL2Role(resourceMap, url, Role.ROLE_PROTECTED_CODE);
            //������ɫȨ������
            List<ReportDefR2Role> r2Roles = reportDef.getReportDefR2Roles();
            for (ReportDefR2Role r2Role : r2Roles) {
                addURL2Role(resourceMap, url, r2Role.getRole().getCode());
            }
        }

        //׷������δ�ܿ�Ȩ��
        addURL2Role(resourceMap, "/layout**", Role.ROLE_ANONYMOUSLY_CODE);

        //Ȩ�޿��Ƶȼ�����ѡֵ˵����
        //high=����δ���ö�ӦȨ�޵�URL������Ϊ�ܱ�����Դ���п��ƣ��������׳����ʾܾ��쳣
        //low= ����δ���ö�ӦȨ�޵�URL������Ϊ�Ǳ�����Դ���ɿ��ƣ�ֻҪ��¼�û������Է���

        if (StringUtils.isNotBlank(authControlLevel) && authControlLevel.trim().equalsIgnoreCase("low")) {
            logger.warn("URL Security control running at level: Low");
            addURL2Role(resourceMap, "/**", Role.ROLE_ANONYMOUSLY_CODE);
        } else {
            logger.info("URL Security control running at level: High");
            addURL2Role(resourceMap, "/**", Role.ROLE_PROTECTED_CODE);
        }

        //ROLE_ADMIN��ɫĬ�ϸ�������Ȩ��
        addURL2Role(resourceMap, "/**", Role.ROLE_ADMIN_CODE);

        if (logger.isDebugEnabled()) {
            logger.debug("Spring Security Resource Mapping:");
            for (Map.Entry<String, Collection<ConfigAttribute>> me : resourceMap.entrySet()) {
                logger.debug(" - {} : {}", me.getKey(), StringUtils.join(me.getValue(), ","));
            }
        }
        return resourceMap;
    }

    private void addURL2Role(Map<String, Collection<ConfigAttribute>> map, String url, String role) {
        for (String splitUrl : url.split("\n")) {
            if (StringUtils.isNotBlank(splitUrl)) {
                splitUrl = splitUrl.replace("?", "*");
                if (!splitUrl.endsWith("**")) {
                    splitUrl = splitUrl + "**";
                }
                Collection<ConfigAttribute> configAttributes = map.get(splitUrl);
                if (configAttributes == null) {
                    configAttributes = new ArrayList<ConfigAttribute>();
                    map.put(splitUrl, configAttributes);
                }
                configAttributes.add(new SecurityConfig(role));
            }
        }
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public Privilege save(Privilege entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void delete(Privilege entity) {
        super.delete(entity);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public List<Privilege> save(Iterable<Privilege> entities) {
        return super.save(entities);
    }

    @Override
    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void delete(Iterable<Privilege> entities) {
        super.delete(entities);
    }

    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void updateRelatedRoleR2s(String id, String[] roleIds) {
        updateRelatedR2s(id, roleIds, "roleR2Privileges", "role");
    }
}
