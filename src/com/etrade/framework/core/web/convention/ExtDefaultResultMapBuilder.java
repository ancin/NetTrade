package com.etrade.framework.core.web.convention;

import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.convention.DefaultResultMapBuilder;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class ExtDefaultResultMapBuilder extends DefaultResultMapBuilder {

    @Inject
    public ExtDefaultResultMapBuilder(ServletContext servletContext, Container container,
            @Inject("struts.convention.relative.result.types") String relativeResultTypes) {
        super(servletContext, container, relativeResultTypes);
    }

    @Override
    protected void makeResults(Class<?> actionClass, String path, String resultPrefix,
            Map<String, ResultConfig> results, PackageConfig packageConfig,
            Map<String, ResultTypeConfig> resultsByExtension) {

        //���ڿ�ܰ�jsp��jsͬǰ׺����������convention�����.js��׺�ļ���Ϊ��������result type�׳��쳣
        //ͨ������Դ��DefaultResultMapBuilder����δ�ṩ������ò����������չ�����ų�.js·�������������쳣�׳�
        if (path != null && path.endsWith(".js")) {
            return;
        }
        super.makeResults(actionClass, path, resultPrefix, results, packageConfig, resultsByExtension);
    }
}
