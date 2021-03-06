package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.Button;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * HTML Button元素封装，主要方便JSP页面基于OGNL的disabled属性控制按钮状态
 * 用法示例：
 * <s3:button cssClass="btn" type="submit" disabled="%{disallowAudit}">
 *    <i class="fa fa-check"></i> 保存
 * </s3:button>
 */
public class S3ButtonTag extends AbstractClosingTag {

    /**  */
    private static final long serialVersionUID = 1L;
    /** 基于HTML Button元素的type类型属性定义：button、reset、submit */
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