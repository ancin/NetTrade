package com.etrade.framework.core.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.exception.DuplicateTokenException;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * 1,Ĭ�ϱ�׼Token������������ã�����ж����б��Ƿ����token���������û����ֱ���׳��쳣
 * ��չ��׼��TokenInterceptor���������������token�Ž��к���У�飬����ֱ������TokenУ��
 * 2,������ٴ�����Token,����JSON����Form�����쳣�����,��JS��ˢ�·�ʽ����form���е�tokenֵ�Ա��û������ٴ���ˢ��ҳ���ύ��
 * 
 */
public class ExtTokenInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 5336675352033972132L;

    private static final Logger logger = LoggerFactory.getLogger(ExtTokenInterceptor.class);

    public static final String TOKEN_COUNTER = "struts.form.submit.counter.token";

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> params = ActionContext.getContext().getParameters();
        if (params.get(TokenHelper.TOKEN_NAME_FIELD) != null) {
            logger.debug("TokenHelper.TOKEN_NAME_FIELD found.");
            HttpServletRequest request = ServletActionContext.getRequest();
            String token = request.getParameter(request.getParameter(TokenHelper.TOKEN_NAME_FIELD));
            HttpSession session = ServletActionContext.getRequest().getSession(true);
            synchronized (session) {
                String counterToken = (String) session.getAttribute(TOKEN_COUNTER);
                if (counterToken != null && token.equals(counterToken)) {
                    throw new DuplicateTokenException("The form has already been processed");
                }
                session.setAttribute(TOKEN_COUNTER, token);
            }
        }
        return invocation.invoke();
    }

}
