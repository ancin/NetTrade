package com.etrade.framework.core.web.convention;

import org.apache.struts2.convention.PackageBasedActionConfigBuilder;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * ����չConvention Plugin������ڣ��Ա���õļ�����tomcat\lib����jar����ģʽ
 */
public class ExtPackageBasedActionConfigBuilder extends PackageBasedActionConfigBuilder {

    @Inject
    public ExtPackageBasedActionConfigBuilder(Configuration configuration, Container container,
            ObjectFactory objectFactory, @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
            @Inject("struts.convention.default.parent.package") String defaultParentPackage) {
        super(configuration, container, objectFactory, redirectToSlash, defaultParentPackage);
    }
}
