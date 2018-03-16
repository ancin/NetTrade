package com.etrade.framework.core.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Persistable;

import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

@MappedSuperclass
@JsonInclude(Include.NON_NULL)
public abstract class PersistableEntity<ID extends Serializable> implements Persistable<ID> {

    /**  */
    private static final long   serialVersionUID                = 1L;

    public static final String EXTRA_ATTRIBUTE_GRID_TREE_LEVEL = "level";

    /**
     * �������ύ��������ʱ����ʶ����������͡�@see RevisionType
     */
    public static final String EXTRA_ATTRIBUTE_OPERATION = "operation";

    /**
     * ����ʾ���ύ����ʱ����ʶ����Ϊ��������Ҫ����
     */
    public static final String EXTRA_ATTRIBUTE_DIRTY_ROW = "dirtyRow";

    /** Entity�������ã���Ҫ����UI�㸨���������� */
    private Map<String, Object> extraAttributes;

    /*
     * ���ڿ����ж϶����Ƿ��½�״̬
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    @Transient
    @JsonIgnore
    public boolean isNew() {
        Serializable id = getId();
        return id == null || StringUtils.isBlank(String.valueOf(id));
    }

    /*
     * ���ڿ����ж϶����Ƿ�༭״̬
     */
    @Transient
    @JsonIgnore
    public boolean isNotNew() {
        return !isNew();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        Persistable that = (Persistable) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @Transient
    public abstract String getDisplay();

    @Transient
    @JsonProperty
    public Map<String, Object> getExtraAttributes() {
        return extraAttributes;
    }

    @Transient
    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    @Transient
    public void addExtraAttribute(String key, Object value) {
        if (extraAttributes == null) {
            extraAttributes = Maps.newHashMap();
        }
        extraAttributes.put(key, value);
    }

    /**
     * ����չ������ȡֵ�жϵ�ǰ�����Ƿ�����Ҫɾ��
     * һ������ǰ��UI�Թ������϶���Ԫ���Ƴ�����
     * @return
     */
    @Transient
    @JsonIgnore
    public boolean isMarkedRemove() {
        if (extraAttributes == null) {
            return false;
        }
        Object opParams = extraAttributes.get(EXTRA_ATTRIBUTE_OPERATION);
        if (opParams == null) {
            return false;
        }
        String op = null;
        if (opParams instanceof String[]) {
            op = ((String[]) opParams)[0];
        } else if (opParams instanceof String) {
            op = (String) opParams;
        }
        if ("remove".equalsIgnoreCase(op)) {
            return true;
        }
        return false;
    }

    @Transient
    @JsonIgnore
    public String getExtraAttributesValue(String key) {
        if (extraAttributes == null) {
            return null;
        }
        Object opParams = extraAttributes.get(key);
        if (opParams == null) {
            return null;
        }
        String op = null;
        if (opParams instanceof String[]) {
            op = ((String[]) opParams)[0];
        } else if (opParams instanceof String) {
            op = (String) opParams;
        }
        return op;
    }

    /**
     * ���ڸ��������������˵��
     * @param lastOperation �硰��ˡ�
     * @return ׷���˵�¼�û�/����ʱ�����Ϣ�Ĳ���˵��
     */
    @Transient
    @JsonIgnore
    public String buildLastOperationSummary(String lastOperation) {
        return AuthContextHolder.getAuthUserPin() + lastOperation + ":" + DateUtils.formatTimeNow();
    }
}
