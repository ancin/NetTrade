package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Files;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * ���������ϴ����
 * ʾ����<s3:files name="search['AD_createdDate']" format="date"/>
 */
public class S3FilesFieldTag extends AbstractClosingTag {

    /**  */
    private static final long serialVersionUID = 1L;

    /** 
     * ���󸽼��б��URL��id�����ǰ�沿�֣���ǩ�Զ��ڴ�ǰ׺���������id=listUrlId����
     */
    protected String listUrlPrefix;

    /**
     * ���󸽼��б��URL��id����ֵ��������������棩Ϊ������ajax���󣬷���ajax���󸽼��б�����
     */
    protected String listUrlId;

    /**
     * �����������ԣ����󲿷����һ��Ĭ��һ��ʵ�����һϵ�и���Ĭ�ϼ���;
     * �����Ҫ���ֲ�ͬ���฽�����岻ͬ��������ֵ����
     */
    protected String category = "default";

    /**
     * ֻ������ֻ��ʾ�����б���������ӻ�ɾ��������ť��һ�����ڲ鿴�����б���ʾ
     */
    protected String readonly = "false";

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Files(stack, req, res);
    }

    @Override
    protected void populateParams() {
        try {
            if (StringUtils.isNotBlank(listUrlId)) {
                this.setDynamicAttribute(null, "data-pk", listUrlId);
            }
            if (StringUtils.isNotBlank(listUrlPrefix)) {
                HttpServletRequest request = ServletActionContext.getRequest();
                this.setDynamicAttribute(null, "data-url", request.getContextPath() + listUrlPrefix);
            }
            if (StringUtils.isNotBlank(category)) {
                this.setDynamicAttribute(null, "data-category", category);
            }
            if (StringUtils.isNotBlank(readonly)) {
                this.setDynamicAttribute(null, "data-readonly", readonly);
            }
        } catch (JspException e) {
            e.printStackTrace();
        }

        super.populateParams();

        Files uiBean = ((Files) component);
        if (theme == null) {
            uiBean.setTheme("bootstrap3");
        }
        if (cssClass == null) {
            uiBean.setCssClass("btn green btn-fileinput-trigger");
        }
    }

    public void setListUrlPrefix(String listUrlPrefix) {
        this.listUrlPrefix = listUrlPrefix;
    }

    public void setListUrlId(String listUrlId) {
        this.listUrlId = listUrlId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

}
