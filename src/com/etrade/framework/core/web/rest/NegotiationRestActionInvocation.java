package com.etrade.framework.core.web.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.RestActionInvocation;

public class NegotiationRestActionInvocation extends RestActionInvocation {

    /**  */
    private static final long serialVersionUID = 1L;

    protected NegotiationRestActionInvocation(Map<String, Object> extraContext, boolean pushAction) {
        super(extraContext, pushAction);
    }

    @Override
    protected void selectTarget() {
        super.selectTarget();
        //��ֵջ���쳣����ŵ�request�����б���errors.jspҳ���ȡʹ��
        Throwable e = (Throwable) stack.findValue("exception");
        if (e != null) {
            HttpServletRequest request = ServletActionContext.getRequest();
            request.setAttribute("struts.rest.error.exception", e);
        }
    }
}
