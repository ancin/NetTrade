package com.etrade.framework.core.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityNotFoundException;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.envers.RevisionType;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.audit.envers.EntityRevision;
import com.etrade.framework.core.audit.envers.ExtDefaultRevisionEntity;
import com.etrade.framework.core.audit.envers.ExtRevisionListener;
import com.etrade.framework.core.entity.BaseEntity;
import com.etrade.framework.core.entity.PersistableEntity;
import com.etrade.framework.core.entity.def.OperationAuditable;
import com.etrade.framework.core.exception.WebException;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.util.DateUtils;
import com.etrade.framework.core.util.ExtStringUtils;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.etrade.framework.core.web.view.OperationResult;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.Preparable;

public abstract class PersistableController<T extends PersistableEntity<ID>, ID extends Serializable>
                                                                                                      extends
                                                                                                      SimpleController
                                                                                                                      implements
                                                                                                                      Preparable {

    private final Logger          logger                       = LoggerFactory
                                                                   .getLogger(PersistableController.class);

    /** Autocomplete������ݵĲ�ѯ���������� */
    protected static final String PARAM_NAME_FOR_AUTOCOMPLETE  = "term";

    /** ����URL���ṩ�˲���ָ��ת���ض�JSPҳ�棬������ͬ������������ͬ���ݣ����ǲ�ͬҵ������Ҫ���ղ�ͬҳ����ʾ�����ָ���˲���ת���ض���ʾJSPҳ��*/
    protected static final String PARAM_NAME_FOR_FORWARD_TO    = "_to_";

    /** ��ҳ��ѯ�����ض������ݴ����ʽ��ʶ������Ĭ�ϱ�ʶ���ز�ѯJSON���ݣ���ָ����xls��ʶ������Ӧ�ģ�����ҳ����ѯ���� */
    protected static final String PARAM_NAME_FOR_EXPORT_FORMAT = "_format_";

    /** ����ָ�����Ͷ�Ӧ��ʵ��Service�ӿڶ��� */
    abstract protected BaseService<T, ID> getEntityService();

    /** ���Ͷ�Ӧ��Class���� */
    protected Class<T>      entityClass;

    /** ���Ͷ�Ӧ��Class���� */
    protected Class<ID>     entityIdClass;

    /** �������ݰ󶨵�Entity����ʵ�� */
    protected T             bindingEntity;

    /** �����������������ݰ󶨵�Entity����ʵ������ */
    protected Collection<T> bindingEntities;

    public String getActionName() {

        //TODO ���ڿ����Ż�Ϊֱ��ע��Struts��ActionNameBuilder��ʽ
        String actionName = this.getClass().getSimpleName();
        String actionSuffix = "Controller";
        if (actionName.equals(actionSuffix))
            throw new IllegalStateException(
                "The action name cannot be the same as the action suffix [" + actionSuffix + "]");

        // Truncate Action suffix if found
        if (actionName.endsWith(actionSuffix)) {
            actionName = actionName.substring(0, actionName.length() - actionSuffix.length());
        }

        // Convert to underscores
        char[] ca = actionName.toCharArray();
        StringBuilder build = new StringBuilder("" + ca[0]);
        boolean lower = true;
        for (int i = 1; i < ca.length; i++) {
            char c = ca[i];
            if (Character.isUpperCase(c) && lower) {
                build.append("-");
                lower = false;
            } else if (!Character.isUpperCase(c)) {
                lower = true;
            }

            build.append(c);
        }

        actionName = build.toString();
        actionName = actionName.toLowerCase();

        return actionName;
    }

    /** ��ModelDriven���ص�model�������⣬����Ŀ��Ʋ���Map�ṹ���ݣ������ҵ���߼�����ҳ�水ť��disabled״̬�� */
    private final Map<String, Object> controlAttributes = new HashMap<String, Object>();

    public Map<String, Object> getControlAttributes() {
        return controlAttributes;
    }

    protected void addControlAttribute(String key, Object value) {
        controlAttributes.put(key, value);
    }

    /**
     * ��ʼ�����췽����������ط��Ͷ���
     */
    @SuppressWarnings("unchecked")
    public PersistableController() {
        super();
        // ͨ������ȡ��Entity��Class.
        try {
            Object genericClz = getClass().getGenericSuperclass();
            if (genericClz instanceof ParameterizedType) {
                entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
                entityIdClass = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[1];
            }
        } catch (Exception e) {
            throw new WebException(e.getMessage(), e);
        }
    }

    // ----------------------------------  
    // -----------��ؽӿڻص�����------------
    // ----------------------------------
    /**
     * ͨ�õ�Preparable�ӿ�prepare�ص��������������id����������ǰ׼��binding�󶨶������ں�������ʹ��
     * ͬʱ�޸���Struts2Ĭ�ϵ�PrepareInterceptorʵ�ַ�ʽ����ִ��prepare��ִ����ص�prepareXXX����
     * see *core.web.interceptor.ExtPrepareInterceptor
     */
    @Override
    public void prepare() {
        ID id = getId("id");
        HttpServletRequest request = this.getRequest();
        if (id != null) {
            //�������POST��ʽ�������ݣ����ȡDetach״̬�Ķ�����������Session��ʽ�Ա��ȡLazy����
            if (request.getMethod().equalsIgnoreCase("POST")) {
                setupDetachedBindingEntity(getId());
            } else {
                bindingEntity = getEntityService().findOne(getId());
            }
            if (bindingEntity != null) {
                checkEntityAclPermission(bindingEntity);
            }
        } else {
            if (request.getMethod().equalsIgnoreCase("POST")) {
                newBindingEntity();
            }
        }
    }

    protected void setupDetachedBindingEntity(ID id) {
        bindingEntity = getEntityService().findDetachedOne(id);
    }

    /**
     * �жϵ�ǰʵ������Ƿ��ѳ־û�����
     * һ������ǰ��ҳ��OGNL�﷨����
     * @return
     */
    public boolean isPersistentedModel() {
        if (bindingEntity != null && bindingEntity.getId() != null
            && String.valueOf(bindingEntity.getId()).trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * ���ݵ�ǰ��¼�û������ݶ�����з��ʿ���Ȩ�޼��
     * Ϊ���ϸ�����û��Ƿ������ݷ��ʣ����Ϊabstractǿ��Ҫ�������ṩʵ�ֶ���
     * @param entity ��update�ɲ����Լ�����
     * @exception ������û��Ȩ�ޣ���ֱ���׳������쳣����
     */
    protected abstract void checkEntityAclPermission(T entity);

    /**
     * ModelDriven�ӿڻص�ʵ�ַ���
     */
    @Override
    public Object getModel() {
        if (model == null) {
            model = bindingEntity;
        }
        return model;
    }

    /**
     * ��id=123��ʽ���ַ���id����ת��ΪID���Ͷ�Ӧ����������ʵ��
     * ���⣬ҳ��Ҳ����Struts��ǩ��ȡ��ʾ��ǰ���������IDֵ
     * @return ID���Ͷ���ʵ��
     */
    public ID getId() {
        return getId("id");
    }

    /**
     * ��ָ������ת��ΪID���Ͷ�Ӧ����������ʵ��
     * ���⣬ҳ��Ҳ����Struts��ǩ��ȡ��ʾ��ǰ���������IDֵ
     * @return ID���Ͷ���ʵ��
     */
    @SuppressWarnings("unchecked")
    public ID getId(String paramName) {
        String entityId = this.getParameter(paramName);
        //jqGrid inline edit�������ݴ���id=������ʶ 
        if (StringUtils.isBlank(entityId) || entityId.startsWith("-")) {
            return null;
        }
        if (String.class.isAssignableFrom(entityIdClass)) {
            return (ID) entityId;
        } else if (Long.class.isAssignableFrom(entityIdClass)) {
            return (ID) (Long.valueOf(entityId));
        } else {
            throw new IllegalStateException("Undefine entity ID class: " + entityIdClass);
        }
    }

    // -------------------------------------
    // -----------ͨ�õ�ҳ��ת������------------
    // -------------------------------------
    /**
     * ���ڴ��������ʱת��ͨ�õ�Tabsҳ��
     * һ�㴴����Tabsҳ��ֻ�е�һ��Tab�ɱ༭������ΪDisabled��״̬
     * @return
     */
    public HttpHeaders inputTabs() {
        return buildDefaultHttpHeaders("inputTabs");
    }

    // -------------------------------------- 
    // -----------View�鿴��������߼�------------
    // -------------------------------------
    /**
     * ��ʾ�鿴Tabsҳ��
     * @return
     */
    public HttpHeaders viewTabs() {
        return buildDefaultHttpHeaders("viewTabs");
    }

    /**
     * ͨ�õ�prepare�ӿڷ������Ѿ�ʵ�ָ���ID׼������ص�binding����
     * ���������Ҫ����������code��������Բ鿴����,�����ҵ���߼���д�˷�������
     */
    public void prepareView() {

    }

    /**
     * �鿴������ʾҳ��
     * @return
     */
    public HttpHeaders view() {
        return buildDefaultHttpHeaders("viewBasic");
    }

    // --------------------------------------  
    // -----------Create������������߼�------------
    // --------------------------------------
    /**
     * ��ʾ����ҳ��֮ǰ׼��newʵ�����
     */
    public void prepareCreate() {
        newBindingEntity();
    }

    /**
     * ת�򴴽�����¼��ҳ��
     * @return
     */
    public HttpHeaders create() {
        return buildDefaultHttpHeaders("inputBasic");
    }

    protected void newBindingEntity() {
        try {
            bindingEntity = entityClass.newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void prepareEdit() {
        String id = this.getParameter("id");
        if (StringUtils.isBlank(id)) {
            newBindingEntity();
        }
    }

    /**
     * �༭������ʾҳ�棬��ͨ�õ�prepare�ӿڷ������Ѿ�׼������ص�binding����
     * ���������Ҫ����create��updateȨ�޿��ƣ�������ô˿��ı༭��ʾ�߼�
     * @return
     */
    public HttpHeaders edit() {
        return buildDefaultHttpHeaders("inputBasic");
    }

    /**
     * ��鵱ǰ�����Ƿ��ֹcreate��������
     * Ĭ�������Ѿ�ǿ��ʵ�� @see #checkEntityAclPermission ����
     * ������ֻ�����ҵ����Ҫ���checkEntityAclPermission֮���create�������Ȩ���߼�
     * һ����������ҵ�񷽷���Ҫcreate��������Ҫ����������ô˷�������create���
     * �˷���������prepareDoCreate�����е��ý��и��¼����;��,����������ǰ��ҳ����OGNL��ʽ����"����"������ť��disabled״̬
     * @param entity ��create�ɲ����Լ�����
     * @return ���ز��������������ʾ��Ϣ�����Ϊ�����ʾ�������
     */
    public String isDisallowCreate() {
        return null;
    }

    /**
     * doCreate����֮ǰ��Preparable�ӿ��Զ��ص�����
     * ׼��newʵ������Ա�ParametersInterceptor���в�����
     */
    public void prepareDoCreate() {
        String msg = isDisallowCreate();
        Assert.isNull(msg, msg);
        newBindingEntity();
    }

    /**
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return JSON������ʾ
     */
    @MetaData(value = "����")
    protected HttpHeaders doCreate() {
        //����ύ�����ݲ��������û�ACLȨ�ޣ�����ܾ���������
        checkEntityAclPermission(bindingEntity);
        ExtRevisionListener.setOperationEvent(RevisionType.ADD.name());
        getEntityService().save(bindingEntity);
        setModel(OperationResult.buildSuccessResult("���������ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    // ---------------------------------------  
    // -----------Update���´�������߼�------------
    // ---------------------------------------
    /**
     * ���¶�����ʾҳ�棬��ͨ�õ�prepare�ӿڷ������Ѿ�׼������ص�binding����
     * 
     * @return
     */
    public HttpHeaders update() {
        return buildDefaultHttpHeaders("inputBasic");
    }

    /**
     * ��鵱ǰ�����Ƿ��ֹupdate���²���
     * Ĭ�������Ѿ�ǿ��ʵ�� @see #checkEntityAclPermission ����
     * ������ֻ�����ҵ����Ҫ���checkEntityAclPermission֮���update�������Ȩ���߼�
     * һ����������ҵ�񷽷���Ҫ���¶�������Ҫ����������ô˷�������update���¼��
     * �˷���������prepareDoUpdate�����е��ý��и��¼����;��,����������ǰ��ҳ����OGNL��ʽ����"����"������ť��disabled״̬
     * @param entity ��update�ɲ����Լ�����
     * @return ���ز��������������ʾ��Ϣ�����Ϊ�����ʾ�������
     */
    public String isDisallowUpdate() {
        return null;
    }

    /**
     * doUpdate����֮ǰ��Preparable�ӿ��Զ��ص�����
     * ����Ĭ�ϵ���checkEntityUpdatePermission���ж������Ȩ�޼��
     * ע�⣺֮���Զ�����ʼ���߼�Ҫ�ŵ�prepare�����Ƕ�Ӧ��doXXX����������Ϊ����Ȩ���ǻ������ݿ��������жϵ�
     * ��prepare��bindingEntity�Ǹմ����ݿ�ȡ�������ݣ���û�н�����ز�����
     * ������doXXX����ʱ�Ķ����Ѿ�������˲����󶨵Ķ���ͨ����ʱ����Ȩ���жϵĶ�������ж����п��ܴ����Ǳ��û��۸�Ȩ������ֵ�ķ���
     * ͬ������������ҵ�񷽷�����ʱ�����Ҳ��Ҫ���ж�������ݼ��ʱ��ҲҪע��˹���Ӧ���ڶ�Ӧ��prepare�ص������н��У�������ҵ��ִ�з�����
     */
    public void prepareDoUpdate() {
        String msg = isDisallowUpdate();
        Assert.isNull(msg, msg);
    }

    /**
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return JSON������ʾ
     */
    @MetaData(value = "����")
    protected HttpHeaders doUpdate() {
        getEntityService().save(bindingEntity);
        setModel(OperationResult.buildSuccessResult("���²����ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    public void prepareDoSave() {
        ID id = getId("id");
        if (id == null) {
            newBindingEntity();
            String msg = isDisallowCreate();
            Assert.isNull(msg, msg);
        } else {
            String msg = isDisallowUpdate();
            Assert.isNull(msg, msg);
        }
    }

    /**
     * ͨ�õ������ύ���洦���������������͸��£����ڴ󲿷ֲ����ֶ���Ȩ�޿��Ƶ�ҵ��ɼ�ʹ�ô˷���
     * ���ҵ������Ҫ�ֿ����ƴ����ͱ༭Ȩ�ޣ���ֱ�����doCreate��doUpdate���ֱ�����URLȨ��
     * @return
     */
    @MetaData(value = "����")
    protected HttpHeaders doSave() {
        getEntityService().save(bindingEntity);
        setModel(OperationResult.buildSuccessResult("���ݱ���ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    // --------------------------------------------- 
    // -----------Deleteɾ�����ݴ�������߼�------------
    // ----------------------------------------------
    /**
     * ��ids=123,234,345�ȸ�ʽ�������ն����зֲ�ת����ѯ��Ӧ��Entity���󼯺ϣ�����ʹ��
     * һ��������ɾ������������
     * @return ʵ����󼯺�
     */
    @SuppressWarnings("unchecked")
    protected Collection<T> getEntitiesByParameterIds() {
        Collection<T> entities = new ArrayList<T>();
        for (String id : getParameterIds()) {
            Object realId = null;
            if (String.class.isAssignableFrom(entityIdClass)) {
                realId = id;
            } else if (Long.class.isAssignableFrom(entityIdClass)) {
                realId = Long.valueOf(id);
            } else {
                throw new IllegalStateException("Undefine entity ID class: " + entityIdClass);
            }
            T entity = getEntityService().findOne((ID) realId);
            entities.add(entity);
        }
        return entities;
    }

    /**
     * ��鵱ǰ�����Ƿ��ֹdelete���²���
     * Ĭ�������Ѿ�ǿ��ʵ�� @see #checkEntityAclPermission ����
     * ������ֻ�����ҵ����Ҫ���checkEntityAclPermission֮���delete�������Ȩ���߼�
     * һ����������ҵ�񷽷���Ҫɾ����������Ҫ����������ô˷�������delete���
     * �˷���������delete�����е��ý��и��¼����;��,����������ǰ��ҳ����OGNL��ʽ����"ɾ��"������ť��disabled״̬
     * @param entity ��delete�ɲ����Լ�����
     * @return ���ز�����ɾ��������ʾ��Ϣ�����Ϊ�����ʾ����ɾ��
     */
    protected String isDisallowDelete(T entity) {
        if (entity.isNew()) {
            return "δ��������";
        }
        return null;
    }

    /**
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return JSON������ʾ
     */
    @MetaData(value = "ɾ��")
    protected HttpHeaders doDelete() {
        //ɾ��ʧ�ܵ�id�Ͷ�Ӧ��Ϣ��Map�ṹ���أ�������ǰ��������ʾ������ʾ�ͼ������������ɾ������
        Map<ID, String> errorMessageMap = Maps.newLinkedHashMap();

        Set<T> enableDeleteEntities = Sets.newHashSet();
        Collection<T> entities = this.getEntitiesByParameterIds();
        for (T entity : entities) {
            //����ACL����Ȩ�޼��
            checkEntityAclPermission(entity);
            //��Ӽ���߼�����ǰ�����Ƿ�����ɾ������״̬���
            String msg = isDisallowDelete(entity);
            if (StringUtils.isBlank(msg)) {
                enableDeleteEntities.add(entity);
            } else {
                errorMessageMap.put(entity.getId(), msg);
            }
        }
        //��������ɾ��,ѭ��ÿ���������Service�ӿ�ɾ��,�������ɾ���������
        //�������Է���ĳЩ����ɾ��ʧ�ܲ�Ӱ����������ɾ��
        //���ҵ���߼���Ҫȷ����������ɾ����ͬһ�������������าд����Service������ɾ���ӿ�
        for (T entity : enableDeleteEntities) {
            try {
                getEntityService().delete(entity);
            } catch (Exception e) {
                logger.warn("entity delete failure", e);
                errorMessageMap.put(entity.getId(), e.getMessage());
            }
        }
        int rejectSize = errorMessageMap.size();
        if (rejectSize == 0) {
            setModel(OperationResult.buildSuccessResult("�ɹ�ɾ����ѡѡȡ��¼:" + entities.size() + "��"));
        } else {
            if (rejectSize == entities.size()) {
                setModel(OperationResult.buildFailureResult("����ѡȡ��¼ɾ������ʧ��", errorMessageMap));
            } else {
                setModel(OperationResult.buildWarningResult("ɾ�������Ѵ���. �ɹ�:"
                                                            + (entities.size() - rejectSize) + "��"
                                                            + ",ʧ��:" + rejectSize + "��",
                    errorMessageMap));
            }
        }
        return buildDefaultHttpHeaders();
    }

    /**
     * ��������õ��������ݴ���ص�����
     * @param op �����������ƣ��硰ȡ����
     * @param entityCallback �ص������ӿ�
     * @return
     */
    protected HttpHeaders processBatchEntities(String op,
                                               EntityProcessCallbackHandler<T> entityCallback) {
        //ɾ��ʧ�ܵ�id�Ͷ�Ӧ��Ϣ��Map�ṹ���أ�������ǰ��������ʾ������ʾ�ͼ������������ɾ������
        Map<ID, String> errorMessageMap = Maps.newLinkedHashMap();

        Collection<T> entities = this.getEntitiesByParameterIds();
        for (T entity : entities) {
            try {
                entityCallback.processEntity(entity);
            } catch (Exception e) {
                logger.warn("entity batch operation failure", e);
                errorMessageMap.put(entity.getId(), e.getMessage());
            }
        }

        int rejectSize = errorMessageMap.size();
        if (rejectSize == 0) {
            setModel(OperationResult.buildSuccessResult("�ɹ�" + op + "��ѡѡȡ��¼:" + entities.size()
                                                        + "��"));
        } else {
            if (rejectSize == entities.size()) {
                setModel(OperationResult
                    .buildFailureResult("����ѡȡ��¼" + op + "����ʧ��", errorMessageMap));
            } else {
                setModel(OperationResult.buildWarningResult(op + "�����Ѵ���. �ɹ�:"
                                                            + (entities.size() - rejectSize) + "��"
                                                            + ",ʧ��:" + rejectSize + "��",
                    errorMessageMap));
            }
        }
        return buildDefaultHttpHeaders();
    }

    // --------------------------------------------- 
    // -----------findByPage��ҳ��ѯ��������߼�------------
    // ----------------------------------------------
    /**
     * ��ҳ�б���ʾ����
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return JSON��������
     */
    @MetaData(value = "��ѯ")
    protected HttpHeaders findByPage() {
        Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(getRequest());
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(entityClass,
            getRequest());
        appendFilterProperty(groupFilter);
        String foramt = this.getParameter(PARAM_NAME_FOR_EXPORT_FORMAT);
        if ("xls".equalsIgnoreCase(foramt)) {
            exportXlsForGrid(groupFilter, pageable.getSort());
        } else {
            setModel(this.getEntityService().findByPage(groupFilter, pageable));
        }
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "������ѡ������")
    protected HttpHeaders selectOptions() {
        Sort sort = PropertyFilter.buildSortFromHttpRequest(getRequest());
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(entityClass,
            getRequest());
        appendFilterProperty(groupFilter);
        setModel(this.getEntityService().findByFilters(groupFilter, sort));
        return new DefaultHttpHeaders();
    }

    /**
     * �������׷�ӹ���������������ڷ�����һ����ڵ�ǰ��¼�û�ǿ��׷�ӹ�������
     * ע�⣺���ǻ��ڵ�ǰ��¼�û����еĿ��Ʋ�����һ����Ҫͨ��ҳ�����������ʽ���ݣ������û��۸��������ݷ��ʷǷ����ݵķ���
     * ���һ��Ҫ��Controller����ͨ����д�˻ص��������Լ���ҵ�񷽷���ǿ��׷�ӹ�������
     * @param filters �ѻ���Request��װ�ò�ѯ�����ļ��϶���
     */
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {

    }

    /**
     * һ�����ڰ�û�з�ҳ�ļ�������ת����װΪ��Ӧ��Page���󣬴��ݵ�ǰ��Grid�����ͳһ��JSON�ṹ������ʾ
     * @param list ���ͼ�������
     * @return ת����װ��Page��ҳ�ṹ����
     */
    protected <S> Page<S> buildPageResultFromList(List<S> list) {
        Page<S> page = new PageImpl<S>(list);
        return page;
    }

    /**
     * ���ҵ����֧�ֶԷ�ҳ��ѯ����Excel��������าд�˷���
     * �����findByPage�����{@link #PARAM_NAME_FOR_EXPORT_FORMAT} �Զ��ص��˷�������Excel���ݵ���
     * @param filters �ѻ���Request��װ�ò�ѯ�����ļ��϶���
     * @param sort �ѻ���Request��װ�õ��������
     * @param groupFilter �ѻ���Request��װ�ø߼���ѯ�����ļ��϶���
     */
    protected void exportXlsForGrid(GroupPropertyFilter groupFilter, Sort sort) {
        throw new UnsupportedOperationException();
    }

    /**
     * ������������ṩ����ز�������, ����JXLS����
     * @see #exportXlsForGrid(List, Sort, GroupPropertyFilter) �˷����л��ڲ�����װ����ص�data���ݺ󣬵��ô˷�������Excel��Ӧ
     * @param dataMap
     */
    protected void exportExcel(String templateFileName, String exportFileName,
                               Map<String, Object> dataMap) {
        //���ڸ�ʽ����
        dataMap.put("dateFormatter", new SimpleDateFormat(DateUtils.DEFAULT_DATE_FORMAT));
        dataMap.put("timeFormatter", new SimpleDateFormat(DateUtils.DEFAULT_TIME_FORMAT));

        HttpServletResponse response = ServletActionContext.getResponse();
        InputStream fis = null;
        OutputStream fos = null;
        try {
            Resource resource = new ClassPathResource("/template/xls/" + templateFileName);
            logger.debug("Open template file inputstream: {}", resource.getURL());
            fis = resource.getInputStream();

            XLSTransformer transformer = new XLSTransformer();
            // generate the excel workbook according to the template and
            // parameters
            Workbook workbook = transformer.transformXLS(fis, dataMap);
            String filename = exportFileName;
            filename = new String(filename.getBytes("GBK"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            fos = response.getOutputStream();
            // output the generated excel file
            workbook.write(fos);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * �ֶ�ֵ�ظ���У��
     * Ψһ����֤URLʾ����id=1&element=masterId&masterId=ABC&additional=referenceId
     * &referenceId=XYZ ������ⲹ���������Щ������ͨ�������ֶι�ͬ����Ψһ�ԣ�����ͨ��additional���������ṩ
     */
    public HttpHeaders checkUnique() {
        String element = this.getParameter("element");
        Assert.notNull(element);
        GroupPropertyFilter groupPropertyFilter = GroupPropertyFilter.buildDefaultAndGroupFilter();

        String value = getRequest().getParameter(element);
        if (!ExtStringUtils.hasChinese(value)) {
            value = ExtStringUtils.encodeUTF8(value);
        }

        groupPropertyFilter.append(new PropertyFilter(entityClass, "EQ_" + element, value));

        // ������ⲹ���������Щ������ͨ�������ֶι�ͬ����Ψһ�ԣ�����ͨ��additional���������ṩ
        String additionalName = getRequest().getParameter("additional");
        if (StringUtils.isNotBlank(additionalName)) {
            String additionalValue = getRequest().getParameter(additionalName);
            if (!ExtStringUtils.hasChinese(additionalValue)) {
                additionalValue = ExtStringUtils.encodeUTF8(additionalValue);
            }
            groupPropertyFilter.append(new PropertyFilter(entityClass, additionalName,
                additionalValue));
        }
        String additionalName2 = getRequest().getParameter("additional2");
        if (StringUtils.isNotBlank(additionalName2)) {
            String additionalValue2 = getRequest().getParameter(additionalName2);
            if (!ExtStringUtils.hasChinese(additionalValue2)) {
                additionalValue2 = ExtStringUtils.encodeUTF8(additionalValue2);
            }
            groupPropertyFilter.append(new PropertyFilter(entityClass, additionalName2,
                additionalValue2));
        }

        List<T> entities = getEntityService().findByFilters(groupPropertyFilter);
        if (entities == null || entities.size() == 0) {// δ�鵽�ظ�����
            this.setModel(Boolean.TRUE);
        } else {
            if (entities.size() == 1) {// ��ѯ��һ���ظ�����
                String id = getRequest().getParameter("id");
                if (StringUtils.isNotBlank(id)) {
                    Serializable entityId = entities.get(0).getId();
                    logger.debug("Check Unique Entity ID = {}", entityId);
                    if (id.equals(entityId.toString())) {// ��ѯ�������ǵ�ǰ�������ݣ������Ѵ���
                        this.setModel(Boolean.TRUE);
                    } else {// ��ѯ�����ݲ��ǵ�ǰ�������ݣ����Ѵ���
                        this.setModel(Boolean.FALSE);
                    }
                } else {// û���ṩSid������˵���Ǵ�����¼�������Ѵ���
                    this.setModel(Boolean.FALSE);
                }
            } else {// ��ѯ������һ���ظ����ݣ�˵�����ݿ����ݱ���������
                this.setModel(Boolean.FALSE);
                throw new WebException("error.check.unique.duplicate: " + element + "=" + value);
            }
        }
        return buildDefaultHttpHeaders();
    }

    // ----------------------------------------------------------------------------------------
    // -----------����Hibernate Envers�������޸ļ�¼��ƹ���------------
    // -----------------------------------------------------------------------------------------
    /**
     * �汾����������ҳ��ת��
     * @return ��struts.xml��ȫ�ֵ�revisionIndex Result����
     */
    public HttpHeaders revisionIndex() {
        return buildDefaultHttpHeaders("revisionIndex");
    }

    /**
     * ���ڰ汾���������б���
     * @return
     */
    public Map<Field, String> getRevisionFields() {
        Map<Field, String> revisionFields = Maps.newLinkedHashMap();
        for (Field field : entityClass.getDeclaredFields()) {
            MetaData metaData = field.getAnnotation(MetaData.class);
            if (metaData != null && metaData.comparable()) {
                revisionFields.put(field, metaData != null ? metaData.value() : field.getName()
                    .toUpperCase());
            }
        }
        return revisionFields;
    }

    /**
     * Revision������¼�б�
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return JSON��������
     */
    @MetaData(value = "�汾�����б�")
    protected HttpHeaders revisionList() {
        String property = this.getParameter("property");
        Boolean hasChanged = null;
        String changed = this.getParameter("changed");
        if (StringUtils.isNotBlank(changed)) {
            hasChanged = BooleanUtils.toBooleanObject(changed);
        }
        List<EntityRevision> entityRevisions = getEntityService().findEntityRevisions(this.getId(),
            property, hasChanged);
        for (EntityRevision entityRevision : entityRevisions) {
            Object entity = entityRevision.getEntity();
            ExtDefaultRevisionEntity revEntity = entityRevision.getRevisionEntity();
            if (entity instanceof OperationAuditable) {
                OperationAuditable aae = (OperationAuditable) entity;
                revEntity.setOldStateDisplay(aae.convertStateToDisplay(revEntity.getOldState()));
                revEntity.setNewStateDisplay(aae.convertStateToDisplay(revEntity.getNewState()));
                revEntity.setOperationEventDisplay(revEntity.getOperationEvent());
            } else {
                revEntity.setOldStateDisplay(revEntity.getOldState());
                revEntity.setNewStateDisplay(revEntity.getNewState());
                revEntity.setOperationEventDisplay(revEntity.getOperationEvent());
            }
        }
        setModel(buildPageResultFromList(entityRevisions));
        return buildDefaultHttpHeaders();
    }

    /**
     * Revision�汾���ݶԱ���ʾ
     * Ϊ�˱�������Ȩ�����ò��ϸ񣬵���δ��Ȩ��Controller���ݲ������ʣ������ṩprotected����ʵ�֣����������Ҫ��дpublicȻ����û��෽��
     * @return ��struts.xml��ȫ�ֵ�revisionCompare Result����
     */
    @MetaData(value = "�汾���ݶԱ�")
    protected HttpHeaders revisionCompare() {
        HttpServletRequest request = this.getRequest();
        ID id = this.getId();
        Long revLeft = Long.valueOf(this.getRequiredParameter("revLeft"));
        Long revRight = Long.valueOf(this.getRequiredParameter("revRight"));
        EntityRevision revLeftEntity = null;
        EntityRevision revRightEntity = null;
        List<EntityRevision> entityRevisions = getEntityService().findEntityRevisions(id, revLeft,
            revRight);
        for (EntityRevision entityRevision : entityRevisions) {
            if (entityRevision.getRevisionEntity().getRev().equals(revLeft)) {
                revLeftEntity = entityRevision;
            } else if (entityRevision.getRevisionEntity().getRev().equals(revRight)) {
                revRightEntity = entityRevision;
            }
        }

        List<Map<String, String>> revEntityProperties = Lists.newArrayList();
        for (Map.Entry<Field, String> me : getRevisionFields().entrySet()) {
            Field field = me.getKey();
            Map<String, String> revEntityProperty = Maps.newHashMap();
            revEntityProperty.put("name", me.getValue());
            if (revLeftEntity != null) {
                try {
                    Object value = FieldUtils.readDeclaredField(revLeftEntity.getEntity(),
                        field.getName(), true);
                    String valueDisplay = convertPropertyDisplay(revLeftEntity.getEntity(), field,
                        value);
                    revEntityProperty.put("revLeftPropertyValue", valueDisplay);
                } catch (IllegalAccessException e) {
                    throw new WebException(e.getMessage(), e);
                }
            }
            if (revRightEntity != null) {
                try {
                    Object value = FieldUtils.readDeclaredField(revRightEntity.getEntity(),
                        field.getName(), true);
                    String valueDisplay = convertPropertyDisplay(revRightEntity.getEntity(), field,
                        value);
                    revEntityProperty.put("revRightPropertyValue", valueDisplay);
                } catch (IllegalAccessException e) {
                    throw new WebException(e.getMessage(), e);
                }

            }
            revEntityProperties.add(revEntityProperty);
        }
        request.setAttribute("revLeftEntity", revLeftEntity);
        request.setAttribute("revRightEntity", revRightEntity);
        request.setAttribute("revEntityProperties", revEntityProperties);
        return buildDefaultHttpHeaders("revisionCompare");
    }

    /**
     * ������Value����ת��Ϊ��ʾ�ַ���������ɸ�����Ҫ��д�˷���������Ƹ�ʽ�ַ���
     * @param entity �汾����ʵ�����
     * @param field �汾�ֶ�����
     * @param value �汾��������ֵ
     * @return ��ʽ��������ַ���
     */
    protected String convertPropertyDisplay(Object entity, Field field, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof PersistableEntity) {
            @SuppressWarnings("rawtypes")
            PersistableEntity persistableEntity = (PersistableEntity) value;
            String label = "N/A";
            try {
                label = persistableEntity.getDisplay();
            } catch (EntityNotFoundException e) {
                //Hibernate EnversĬ��ʼ�ղ�ѯ��ӦAudit�汾���ݣ��п��ܹ�������֮ǰû��Audit��¼���Ӷ��ᵼ��Envers�׳�δ�ҵ������쳣
                //�˴���Hack�������û���ҵ�����Audit��¼�����ѯ�����������¼
                try {
                    //��Hibernate AOP��ǿ���󷴲��Ӧʵ���������
                    JavassistLazyInitializer jli = (JavassistLazyInitializer) FieldUtils
                        .readDeclaredField(value, "handler", true);
                    @SuppressWarnings("rawtypes")
                    Class entityClass = jli.getPersistentClass();
                    Serializable id = jli.getIdentifier();
                    Object obj = getEntityService().findEntity(entityClass, id);
                    @SuppressWarnings("rawtypes")
                    PersistableEntity auditTargetEntity = (PersistableEntity) obj;
                    label = auditTargetEntity.getDisplay();
                } catch (IllegalAccessException iae) {
                    logger.warn(e.getMessage());
                }
            }
            return label;
        }
        return String.valueOf(value);
    }

    private static Map<Class<?>, Map<String, Object>> entityValidationRulesMap = Maps.newHashMap();

    /**
     * ֧�ֵ�ת�������б�
     * <ul>
     * <li>@Email   email�����ʼ���ʽ</li>
     * <li>@Column(nullable=false)   required���ݱ���</li>
     * </ul> 
     */
    @Override
    @MetaData(value = "������ݱ༭У�����")
    public HttpHeaders buildValidateRules() {
        try {
            Map<String, Object> nameRules = entityValidationRulesMap.get(entityClass);
            if (nameRules == null) {
                nameRules = Maps.newHashMap();
                entityValidationRulesMap.put(entityClass, nameRules);

                Class<?> clazz = entityClass;
                Set<Field> fields = Sets.newHashSet(clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
                while (!clazz.equals(BaseEntity.class) && !clazz.equals(Object.class)) {
                    fields.addAll(Sets.newHashSet(clazz.getDeclaredFields()));
                    clazz = clazz.getSuperclass();
                }

                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())
                        || !Modifier.isPrivate(field.getModifiers())
                        || Collection.class.isAssignableFrom(field.getType())) {
                        continue;
                    }
                    String name = field.getName();
                    if ("id".equals(name)) {
                        continue;
                    }
                    Map<String, Object> rules = Maps.newHashMap();

                    MetaData metaData = field.getAnnotation(MetaData.class);
                    if (metaData != null) {
                        String tooltips = metaData.tooltips();
                        if (StringUtils.isNotBlank(tooltips)) {
                            rules.put("tooltips", tooltips);
                        }
                    }

                    Method method = MethodUtils.getAccessibleMethod(entityClass,
                        "get" + StringUtils.capitalize(name), null);

                    if (method != null) {
                        Class<?> retType = method.getReturnType();
                        Column column = method.getAnnotation(Column.class);

                        if (column != null) {
                            if (retType != Boolean.class && column.nullable() == false) {
                                rules.put("required", true);
                            }
                            if (column.unique() == true) {
                                rules.put("unique", true);
                            }
                            if (column.updatable() == false) {
                                rules.put("readonly", true);
                            }
                            if (column.length() > 0 && retType == String.class
                                && method.getAnnotation(Lob.class) == null) {
                                rules.put("maxlength", column.length());
                            }
                        }

                        JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
                        if (joinColumn != null) {
                            if (joinColumn.nullable() == false) {
                                rules.put("required", true);
                            }
                        }

                        if (retType == Date.class) {
                            JsonSerialize jsonSerialize = method.getAnnotation(JsonSerialize.class);
                            if (jsonSerialize != null) {
                                if (DateJsonSerializer.class == jsonSerialize.using()) {
                                    rules.put("date", true);
                                } else if (DateTimeJsonSerializer.class == jsonSerialize.using()) {
                                    rules.put("timestamp", true);
                                }
                            } else {
                                rules.put("date", true);
                            }
                        } else if (retType == BigDecimal.class) {
                            rules.put("number", true);
                        } else if (retType == Integer.class || retType == Long.class) {
                            rules.put("integer", true);
                        }

                        Size size = method.getAnnotation(Size.class);
                        if (size != null) {
                            if (size.min() > 0) {

                            }
                            if (size.max() < Integer.MAX_VALUE) {
                            }
                        }

                        Email email = method.getAnnotation(Email.class);
                        if (email != null) {
                            rules.put("email", true);
                        }

                        Pattern pattern = method.getAnnotation(Pattern.class);
                        if (pattern != null) {
                            rules.put("regex", pattern.regexp());
                        }

                        if (rules.size() > 0) {
                            nameRules.put(name, rules);
                            //�����ʵ��������ͣ�һ���Ԫ��name������Ϊentity.id����˶���׷�Ӷ�Ӧid����У�����
                            if (PersistableEntity.class.isAssignableFrom(field.getType())) {
                                nameRules.put(name + ".id", rules);
                            }
                        }
                    }
                }
            }
            setModel(nameRules);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setModel(OperationResult.buildFailureResult("ϵͳ�����쳣"));
        }

        return new DefaultHttpHeaders();
    }

    /**
     * ���ڷ���;ۺ����Է���Map�ṹ��ҳ����
     * �жϹ����������ư���"("���ʶΪ�ۺ����ԣ�����Ϊ��������
     * @param properties  �����б��﷨����sum = + , diff = - , prod = * , quot = / , case(condition,when,else)
     * ʾ����
     *     sum(amount)
     *     sum(diff(amount,costAmount))
     *     min(case(equal(amount,0),-1,quot(diff(amount,costAmount),amount)))
     *     case(equal(sum(amount),0),-1,quot(sum(diff(amount,costAmount)),sum(amount)))
     * @param groupFilter  ��̬��������
     * @param pageable  ��ҳ�������
     * @return
     */
    protected Page<Map<String, Object>> findByGroupAggregate(GroupPropertyFilter groupFilter,
                                                             Pageable pageable,
                                                             String... properties) {
        return getEntityService().findByGroupAggregate(groupFilter, pageable, properties);
    }

    /**
     * ���ڲ�����������÷����ѯ 
     * @param properties
     * @param groupFilter  ��̬��������
     * @param sort  �������
     * @return
     */
    protected Page<Map<String, Object>> findByGroupAggregate(GroupPropertyFilter groupFilter,
                                                             Sort sort, String... properties) {
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE, sort);
        return findByGroupAggregate(groupFilter, pageable, properties);
    }

    /**
     * ���ڲ������÷����ѯ 
     * @param properties
     * @param groupFilter  ��̬��������
     * @return
     */
    protected Page<Map<String, Object>> findByGroupAggregate(GroupPropertyFilter groupFilter,
                                                             String... properties) {
        return getEntityService().findByGroupAggregate(groupFilter, null, properties);
    }

    /**
     * ����������װ�Ĳ������÷����ѯ 
     * @param properties
     * @return
     */
    protected Page<Map<String, Object>> findByGroupAggregate(String... properties) {
        Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(getRequest());
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(entityClass,
            getRequest());
        appendFilterProperty(groupFilter);
        return findByGroupAggregate(groupFilter, pageable, properties);
    }

    /**
     * ���ඨ��ɽ��ܲ������飬Ȼ����ôΰ����෽�����в����ɽ��ܼ��
     * ������Ҫ���ⲿ�û����ʵ�ControllerΪ�˱����û��Ƿ��۸�����
     * �������Ͳ������������Ǻ���Ϊ�����±�ƥ�䣬abc[*].xyz��ʽ����
     * �� @see ParameterNameAware ��ʽ�����Զ��󶨲���������
     */
    protected boolean acceptableParameterName(String[] acceptableParameterNames,
                                              String parameterName) {
        if (parameterName.equals("struts.token.name") || parameterName.equals("token")
            || parameterName.equals("version")) {
            return true;
        }
        if (parameterName.equals("extraAttributes")
            || parameterName.indexOf("extraAttributes.") > -1) {
            return true;
        }
        //�������Ͳ�����ת���Ա�ƥ��abc[*].xyz����ģʽ
        if (parameterName.indexOf("[") > -1) {
            parameterName = StringUtils.substringBefore(parameterName, "[") + "[*]"
                            + StringUtils.substringAfter(parameterName, "]");
        }
        for (String name : acceptableParameterNames) {
            if (name.equals(parameterName)) {
                return true;
            }
            if (name.indexOf("[") > -1) {
                name = name.replace("[*]", "");
            }
            //Ƕ�ײ���֧��
            if (name.indexOf(parameterName + ".") > -1 || name.indexOf("." + parameterName) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * ����һЩ���Ӵ����߼���Ҫ�����ύ���ݷ�����У�������ʾ������Ϣ��Ҫ�û�����ȷ��
     * �жϵ�ǰ���Ƿ��ѱ��û�confirmȷ��OK
     */
    protected boolean postNotConfirmedByUser() {
        return !BooleanUtils.toBoolean(getParameter("_serverValidationConfirmed_"));
    }
}
