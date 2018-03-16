package com.etrade.framework.core.web.interceptor;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

import com.etrade.framework.core.common.PersistableController;
import com.etrade.framework.core.entity.BaseEntity;
import com.etrade.framework.core.entity.PersistableEntity;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.exception.WebException;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 1��Struts�Ĳ����󶨻��ƿ��Դ���򻯲�����������,���ǻ����Ǳ�ڵı���������:�û�����һЩ�Ƿ��������ύһЩ����Ҫ�Ĳ����󶨴Ӷ����·Ƿ��������޸�
 * �˴�ΪParametersInterceptor��չ��ӻ���Annotationע���Skip�����趨,����һЩ���е�ʵ�����Կ����@see ParamBindIgnore
 * ������Struts�ĶԴ����Ե��Զ��󶨴Ӷ������������ֵ�޸�
 * 2��ȫ�ַ��䴦��һ��һ���������������������һ�Զ�Ĺ�������Ԫ�ء�remove����������
 * TODO: �˴�����Ҫ��Ϊ����ͨ��ģ�ͼ򻯿��������������ǿ��ܴ���һ��������ģ�������ĳ̶���Ҫ��һ�����ܲ�����֤��
 * �������Ӧ�ñȽϽ����������ؿ��Կ���ȥ��ͨ�ô����߼����ڸ�ҵ������ж��ƻ�ʵ�֡�
 */
public class ExtParametersInterceptor extends ParametersInterceptor {

    /**  */
    private static final long   serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ExtParametersInterceptor.class);

    @Override
    protected boolean isAccepted(String paramName) {
        boolean matches = super.isAccepted(paramName);
        if (matches) {
            try {
                CompoundRoot root = ActionContext.getContext().getValueStack().getRoot();
                for (Object obj : root) {
                    if (obj != null && BaseEntity.class.isAssignableFrom(obj.getClass())) {
                        Method method = OgnlRuntime.getSetMethod((OgnlContext) ActionContext.getContext()
                                .getContextMap(), obj.getClass(), paramName);
                        if (method != null) {
                            SkipParamBind mvcAutoBindExclude = method.getAnnotation(SkipParamBind.class);
                            if (mvcAutoBindExclude != null) {
                                matches = false;
                                logger.info(
                                        "Skip auto bind parameter to model property according MvcAutoBind annotation: {} : {}",
                                        obj.getClass(), paramName);
                            }
                        }
                    }
                }
            } catch (IntrospectionException e) {
                logger.error(e.getMessage(), e);
            } catch (OgnlException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return matches;
    }

    @Override
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        Map<String, Object> params = ac.getParameters();
        //id�����Ƴ������Զ��󶨣����ñ�׼��request.getParameter��ȡ��������ѯʵ�����
        params.remove("id");
        return params;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setParameters(final Object action, ValueStack stack, final Map<String, Object> parameters) {
        super.setParameters(action, stack, parameters);

        if (action instanceof PersistableController) {
            HttpServletRequest request = ServletActionContext.getRequest();
            if (request.getMethod().equalsIgnoreCase("POST")) {
                PersistableController pc = (PersistableController) action;
                Object model = pc.getModel();
                if (model != null && model instanceof Persistable) {
                    try {
                        Persistable entity = (Persistable) model;
                        Set<String> needRemoveElementsPropertyNames = Sets.newHashSet();
                        for (String key : parameters.keySet()) {
                            if (key.endsWith(".id")) {
                                //���ڹ�����������StrutsĬ������ΪNewһ������ʵ���Խ��к����Ĳ������ݰ�
                                //�ڰѹ���OneToOne�����޸�ΪΪ��ʱ����Ҫ�������⴦���԰ѡ������ݡ���ʾ���������û�null
                                //�Ա���JPA������ʵ��merge����ʱ�׳�δ����ʵ��������
                                String name = StringUtils.substringBeforeLast(key, ".id");
                                String value = request.getParameter(key);
                                //id��ֵ�������账�����ѭ��
                                if (StringUtils.isNotBlank(value)) {
                                    continue;
                                }

                                //�������жϵ�ǰ�����Ƕ�����������ύ���Ǽ򵥵�ֻ�ǹ��������ύ
                                //.displayһ������������comboxѡȡ�����Ԫ�أ���������Ч�Ķ��������ύ����
                                //������ֳ���֮��Ĺ����������ݣ�˵���ǹ����������ݱ����������������Ķ��������洦��
                                int cnt = 0;
                                for (String param : parameters.keySet()) {
                                    if (param.startsWith(name + ".") && !param.equals(name + ".display")
                                            && !param.equals(name + ".id")) {
                                        cnt++;
                                        break;
                                    }
                                }

                                //���������Ϊ0��˵����ǰ���Ƕ������ݱ༭ģʽ�����Ǽ򵥵Ĺ���������ģʽ
                                if (cnt == 0) {
                                    logger.debug("Reset [{}] OneToOne [{}] to null as empty id value", model, name);
                                    if (name.indexOf("[") > -1 && name.indexOf("]") > -1) {
                                        //������������
                                        String fisrtPropName = StringUtils.substringBefore(name, "[");
                                        int idx = Integer.valueOf(StringUtils.substringBetween(name, "[", "]"));
                                        List items = (List) MethodUtils.invokeMethod(model,
                                                "get" + StringUtils.capitalize(fisrtPropName), null);
                                        String lastPropName = StringUtils.substringAfter(name, "]");
                                        if (StringUtils.isBlank(lastPropName)) {
                                            items.remove(idx);
                                        } else {
                                            Object item = items.get(idx);
                                            //TODO Ŀǰֻ֧�ֵ��㣬��Ҫ��Ӷ��Ƕ�״����߼�
                                            String fieldName = StringUtils.substringAfter(lastPropName, ".");
                                            if (FieldUtils.getDeclaredField(item.getClass(), fieldName, true) != null) {
                                                FieldUtils.writeDeclaredField(item, fieldName, null, true);
                                            }
                                        }
                                    } else {
                                        //��һ���ԣ�ֱ�Ӱѹ�����������Ϊnull�������������ݵ���˻���Ϊ��һ��û���κ����ݵ�����Ҫ�´����ļ����������
                                        //TODO Ŀǰֻ֧�ֵ��㣬��Ҫ��Ӷ��Ƕ�״����߼�
                                        if (FieldUtils.getDeclaredField(model.getClass(), name, true) != null) {
                                            FieldUtils.writeDeclaredField(model, name, null, true);
                                        }
                                    }
                                }
                            } else if (key.endsWith(".extraAttributes.operation")) {
                                //������Ҫ����remove����ļ���Ԫ������
                                //purchaseOrderDetails[1].extraAttributes.operation=remove
                                if (!entity.isNew()) {
                                    String value = request.getParameter(key);
                                    if ("remove".equals(value)) {
                                        String name = StringUtils
                                                .substringBeforeLast(key, ".extraAttributes.operation");
                                        name = StringUtils.substringBeforeLast(name, "[");
                                        needRemoveElementsPropertyNames.add(name);
                                    }
                                }
                            }
                        }

                        //���ڰ���remove�Ƴ�����ļ������Խ���������
                        if (!entity.isNew()) {
                            for (String propName : needRemoveElementsPropertyNames) {
                                Method method = OgnlRuntime.getGetMethod(null, model.getClass(), propName);
                                Collection r2s = (Collection) method.invoke(model);
                                for (Iterator iter = r2s.iterator(); iter.hasNext();) {
                                    PersistableEntity persistable = (PersistableEntity) iter.next();
                                    if (persistable.isMarkedRemove()) {
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new WebException("error.hack.empty.onetoone.entity", e);
                    }
                }
            }
        }
    }
}
