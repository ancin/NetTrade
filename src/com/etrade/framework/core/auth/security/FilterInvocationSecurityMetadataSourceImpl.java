package com.etrade.framework.core.auth.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import com.etrade.framework.core.auth.service.PrivilegeService;
import com.google.common.collect.Lists;

public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    private final Logger logger = LoggerFactory.getLogger(FilterInvocationSecurityMetadataSourceImpl.class);

    private PrivilegeService privilegeService;

    private static AntPathMatcher urlMatcher = new AntPathMatcher();

    public void setPrivilegeService(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        logger.debug("Just return null for getAllConfigAttributes...");
        /**
         * �����Ҫ������ʱ����Ȩ��������֤����ȡ������ע�ͳ�ʼ���߼�       
        Map<String, Collection<ConfigAttribute>> resourceMap= privilegeService.loadResourceDefine();
        Collection<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
        for (Collection<ConfigAttribute> ca : resourceMap.values()) {
            configAttributes.addAll(ca);
        }
        return configAttributes;
        */
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    //������������Դ����Ҫ��Ȩ��
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //�˴�����Spring��Cache���Ʊ���ÿ�β�ѯ���ݿ�
        Map<String, Collection<ConfigAttribute>> resourceMap = privilegeService
            .loadResourceDefine();
        
        String url=null;
        if (object instanceof String) {
            url = (String) object;
        } else {
            url = ((FilterInvocation) object).getRequestUrl();
        }
        Assert.notNull(url);
        Iterator<String> ite = resourceMap.keySet().iterator();
        while (ite.hasNext()) {
            String resURL = ite.next();
            if (urlMatcher.match(resURL, url)) {
                return resourceMap.get(resURL);
            }
        }
        return Lists.newArrayList();
    }

}
