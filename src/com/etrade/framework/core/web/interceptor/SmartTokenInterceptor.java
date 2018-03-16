package com.etrade.framework.core.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Ĭ�ϱ�׼Token������������ã�����ж����б��Ƿ����token���������û����ֱ���׳��쳣
 * ��չ��׼��TokenInterceptor���������������token�Ž��к���У�飬����ֱ������TokenУ��
 * ����ǰToken��Ǩ������������,��ǰ��Exception�����߼���,�ӱ������Իָ�Token,�Ӷ�ʹ�û������ٴ��ύͬһ��token��
 */
public class SmartTokenInterceptor extends TokenInterceptor {

    /**  */
    private static final long   serialVersionUID   = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SmartTokenInterceptor.class);

    public static final String BACKUP_TOKEN_VALUE = "backup.token.value";

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> params = ActionContext.getContext().getParameters();
        if (!params.containsKey(TokenHelper.TOKEN_NAME_FIELD)) {
            //logger.debug("TokenHelper.TOKEN_NAME_FIELD not found. Skip token interceptor.");
            return invocation.invoke();
        } else {
            logger.debug("TokenHelper.TOKEN_NAME_FIELD found.");

            //��Token���浽BACKUP_TOKEN_VALUE�����У��Ա��ڳ����쳣ʱ�ָ�token��ʹ���û������ٴ��ύ
            String tokenName = TokenHelper.getTokenName();
            Map<String, Object> session = ActionContext.getContext().getSession();
            String tokenSessionName = TokenHelper.buildTokenSessionAttributeName(tokenName);
            String sessionToken = (String) session.get(tokenSessionName);
            HttpSession httpSession= ServletActionContext.getRequest().getSession();
            httpSession.setAttribute(BACKUP_TOKEN_VALUE, sessionToken);

            logger.debug("Invoke token interceptor.");
            return super.doIntercept(invocation);

        }
    }

}
