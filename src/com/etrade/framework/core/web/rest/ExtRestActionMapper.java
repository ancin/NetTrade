package com.etrade.framework.core.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.rest.RestActionMapper;

import com.etrade.framework.core.audit.envers.ExtRevisionListener;
import com.opensymphony.xwork2.config.ConfigurationManager;

/**
 * ��չ��׼��REST���������߼�
 * 1. ��Ӷ�;jsessionid=����ϴ����
 * 2. ��Action��������ΪHibernate Envers��������¼���д���̶߳������ݶ���
 */
public class ExtRestActionMapper extends RestActionMapper {

    @Override
    protected void parseNameAndNamespace(String uri, ActionMapping mapping, ConfigurationManager configManager) {
        super.parseNameAndNamespace(uri, mapping, configManager);
        String name = mapping.getName();
        // cut off any ;jsessionid= type appendix but allow the rails-like ;edit
        int scPos = name.indexOf(';');
        if (scPos > -1 && !"edit".equals(name.substring(scPos + 1))) {
            name = name.substring(0, scPos);
            mapping.setName(name);
        }

    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping actionMapping = super.getMapping(request, configManager);
        //��Action��������ΪHibernate Envers��������¼���д���̶߳������ݶ���
        //TODO: ���Կ��ǽ�һ���Ż�ȡ������Ӧ��metadata������Ϣ����ʹ�����Ϣ��ʾ���Ѻ�
        if (actionMapping != null) {
            String path = actionMapping.getNamespace() + "/" + actionMapping.getName();
            if (StringUtils.isNotBlank(actionMapping.getMethod())) {
                path += ("!" + actionMapping.getMethod());
            }
            //�������Լ�¼��ǰ�����action��ʽ��url·��
            //��һЩͨ��·�������߼�����ʹ�ã���revision-index.jsp
            request.setAttribute("etrade.struts.action.url", path);
            ExtRevisionListener.setOperationEvent(path);
        }
        return actionMapping;
    }
}
