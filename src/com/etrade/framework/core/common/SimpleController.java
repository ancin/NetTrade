package com.etrade.framework.core.common;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.exception.WebException;
import com.etrade.framework.core.security.AuthContextHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ModelDriven;

public abstract class SimpleController implements ModelDriven<Object> {

    private final Logger logger = LoggerFactory.getLogger(SimpleController.class);

    /** ����URL���ṩ�˲���ָ��ת���ض�JSPҳ�棬������ͬ������������ͬ���ݣ����ǲ�ͬҵ������Ҫ���ղ�ͬҳ����ʾ�����ָ���˲���ת���ض���ʾJSPҳ��*/
    protected static final String PARAM_NAME_FOR_FORWARD_TO = "_to_";

    /** ModelDriven���� */
    protected Object model = null;

    /** ǰ�˱�һЩ����Ҫ����ı�Ԫ�ص�name����Ϊignore��������Struts������OGNL�쳣 */
    protected String ignore = null;

    /**
     * ModelDriven�ӿڻص�ʵ�ַ���
     */
    @Override
    public Object getModel() {
        return model;
    }

    /**
     * �������෽���޸����÷��ص�ModelDrivenģ�Ͷ���
     * @param model
     */
    protected void setModel(Object model) {
        this.model = model;
    }

    /**
     * һ��������ɾ������������
     * @return id�ַ�������
     */
    protected String[] getParameterIds() {
        return getParameterIds("ids");
    }

    /**
     * һ��������ɾ������������
     * @return id�ַ�������
     */
    protected String[] getParameterIds(String paramName) {
        HttpServletRequest request = ServletActionContext.getRequest();
        Set<String> idSet = Sets.newHashSet();
        String[] params = request.getParameterValues(paramName);
        if (params != null) {
            for (String param : params) {
                for (String id : param.split(",")) {
                    String trimId = id.trim();
                    if (StringUtils.isNotBlank(trimId)) {
                        idSet.add(trimId);
                    }
                }
            }
        }
        return idSet.toArray(new String[] {});
    }

    /**
     * ����Ĭ�ϵ�REST������Ӧ��һ������ֱ��JSON�������
     * @return
     */
    protected DefaultHttpHeaders buildDefaultHttpHeaders() {
        return new DefaultHttpHeaders().disableCaching();
    }

    /**
     * ����code��������Ĭ�ϵ�REST������Ӧ��һ������JSPҳ��ת����ʾ����
     * @return
     */
    protected DefaultHttpHeaders buildDefaultHttpHeaders(String code) {
        String to = this.getParameter(PARAM_NAME_FOR_FORWARD_TO);
        if (StringUtils.isNotBlank(to)) {
            code = to;
        }
        return new DefaultHttpHeaders(code).disableCaching();
    }

    /**
     * �����෽���������ȡHttpServletRequest
     * 
     * @return
     */
    protected HttpServletRequest getRequest() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return request;
    }

    /**
     * �����෽���������ȡHttpServletResponse
     * 
     * @return
     */
    protected HttpServletResponse getResponse() {
        HttpServletResponse response = ServletActionContext.getResponse();
        return response;
    }

    // ----------------------------------  
    // -----------���������������෽��--------
    // ----------------------------------

    /**
     * ��ȡ�������ֵ,�������Ϊ�����׳��쳣
     * 
     * @param name ��������
     * @return ����ֵ
     */
    protected String getRequiredParameter(String name) {
        String value = getRequest().getParameter(name);
        if (StringUtils.isBlank(value)) {
            throw new WebException("web.param.disallow.empty: " + name);
        }
        return value;
    }

    /**
     * ��ȡ����ֵ,���δ�հ��򷵻�ȱʡֵ
     * 
     * @param name ��������
     * @param name �������Ϊ�շ��ص�Ĭ��ֵ
     * @return ����ֵ
     */
    protected String getParameter(String name, String defaultValue) {
        String value = getRequest().getParameter(name);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * ���淽ʽ��ȡ�������ֵ
     * 
     * @param name  ��������
     * @return ����ֵ
     */
    protected String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    // ----------------------------------  
    // -----------OGNL��������෽��---------
    // ----------------------------------
    /**
     * ����OGNL�ж��ַ�����ΪBlank
     * @param str �ж��ַ���
     * @return
     */
    public boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * ����OGNL�ж��ַ���ΪBlank
     * @param str �ж��ַ���
     * @return
     */
    public boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /** 
     * ǰ�˱�һЩ����Ҫ����ı�Ԫ�ص�name����Ϊignore��������Struts������OGNL�쳣 
     */
    public void setIgnore(String ignore) {
        //do nothing
    }

    // -------------------------------------
    // -----------ͨ�õ�ҳ��ת������------------
    // -------------------------------------
    /**
     * ��ʾĬ��Index������
     * @return
     */
    public HttpHeaders index() {
        return buildDefaultHttpHeaders("index");
    }

    /**
     * ͨ��forwarת�򷽷�������toת�򵽶�Ӧ��JSPҳ�� ���to����Ϊ�գ����׳���Ҫ����ȱʧ�쳣
     * 
     * @return
     */
    public String forward() {
        String to = this.getRequiredParameter(PARAM_NAME_FOR_FORWARD_TO);
        logger.debug("Direct forward to: {}", to);
        return to;
    }

    /**
     * ռλ�������壬��ʵ�ʴ����߼���ϸ���߼������ඨ��
     */
    @MetaData(value = "������ݱ༭У�����")
    public HttpHeaders buildValidateRules() {
        setModel(Maps.newHashMap());
        return buildDefaultHttpHeaders();
    }

    /**
     * ��ȡ��ǰ��¼�û��ʺ�
     * @return
     */
    public String getSigninUsername() {
        return AuthContextHolder.getAuthUserPin();
    }
}
