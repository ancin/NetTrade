package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.Button;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * HTML ButtonԪ�ط�װ����Ҫ����JSPҳ�����OGNL��disabled���Կ��ư�ť״̬
 * �÷�ʾ����
 * <s3:button cssClass="btn" type="submit" disabled="%{disallowAudit}">
 *    <i class="fa fa-check"></i> ����
 * </s3:button>
 */
public class S3ButtonTag extends AbstractClosingTag {

    /**  */
    private static final long serialVersionUID = 1L;
    /** ����HTML ButtonԪ�ص�type�������Զ��壺button��reset��submit */
    protected String type;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Button(stack, req, res);
    }

    @Override
    protected void populateParams() {
        try {
            if (StringUtils.isNotBlank(type)) {
                this.setDynamicAttribute(null, "type", type);
            }
        } catch (JspException e) {
            e.printStackTrace();
        }

        super.populateParams();

        Button uiBean = ((Button) component);
        if (theme == null) {
            uiBean.setTheme("bootstrap3");
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}