package org.apache.struts2.views.jsp;

import java.util.Collection;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.etrade.framework.core.security.AuthContextHolder;
import com.google.common.collect.Sets;

/**
 * ���ڵ�ǰ��¼�û���Ȩ�޴��뼯�ϣ�����code���Ժ�Ȩ�ޱȽ�type���ͣ�����ҳ��Ԫ�ص���ʾ
 * �÷�ʾ���� <s2:privilege code="P001">�ܿ�HML���ݣ��簴ť���ı���</s2:privilege>
 */
public class S2PrivilegeTag extends TagSupport {

    /**  */
    private static final long serialVersionUID = 1L;

    /** Ȩ�޴��룬����Ƕ���ɶ��ŷָ� */
    private String code;

    /** ��Ȩ�޴��봦������: hasAny=�κ�Ȩ�޷�����ͨ��,hasAll=Ҫ��ȫ��ƥ���ͨ��,notAll=���ж����벻ƥ��, notAny=�κ�һ��������ƥ�� */
    private final String type = "hasAny";

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int doStartTag() throws JspException {
        Collection<String> authCodes = AuthContextHolder.getAuthUserDetails().getPrivilegeCodes();
        Set<String> codes = Sets.newHashSet(StringUtils.split(code, ","));
        if("hasAll".equalsIgnoreCase(type)){
            for (String c : codes) {
                if(!authCodes.contains(c.trim())){
                    return SKIP_BODY;
                }
            }
            return EVAL_BODY_INCLUDE;
        }else if("hasAny".equalsIgnoreCase(type)){
            for (String c : codes) {
                if(authCodes.contains(c.trim())){
                    return EVAL_BODY_INCLUDE;
                }
            }
            return SKIP_BODY;
        }else if("notAll".equalsIgnoreCase(type)){
            for (String c : codes) {
                if(authCodes.contains(c.trim())){
                    return SKIP_BODY;
                }
            }
            return EVAL_BODY_INCLUDE;
        }else if("notAny".equalsIgnoreCase(type)){
            for (String c : codes) {
                if(!authCodes.contains(c.trim())){
                    return EVAL_BODY_INCLUDE;
                }
            }
            return SKIP_BODY;
        }else{
            throw new IllegalArgumentException("Undefined type parameter: "+type);
        }
    }
}
