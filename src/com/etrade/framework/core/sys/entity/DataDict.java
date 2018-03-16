package com.etrade.framework.core.sys.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.etrade.framework.core.sys.service.DataDictService;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TBL_SYS_DATA_DICT", uniqueConstraints = @UniqueConstraint(columnNames = { "PARENT_ID", "primaryKey",
        "secondaryKey" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "�����ֵ�")
public class DataDict extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    /** 
     * �ֵ����ݵ�����ʶ�����󲿷�������ڵ�һ����ʶ����ȷ��Ψһ�Ե��ֵ�����ֻ��ά�����ֶ�ֵ����
     * ע�⣺primaryKey+secondaryKey+parentΨһ��Լ��
     */
    @MetaData(value = "����ʶ")
    @EntityAutoCode(order = 8, search = true)
    private String primaryKey;

    /** 
     * �ֵ����ݵ�secondaryKeyֵ�����primaryKeyֵ���ܵ�һȷ��Ψһ�����������secondaryKeyֵ�������Ψһ����
     */
    @MetaData(value = "�α�ʶ")
    @EntityAutoCode(order = 10, search = true)
    private String secondaryKey;

    /**
     * �ֵ����ݶ�Ӧ������Valueֵ
     * �󲿷����һ�㶼��key-value��ʽ�����ݣ�ֻ��Ҫά��primaryKey��primaryValue���ɣ�
     * Ȼ��ͨ��{@link DataDictService#findChildrenByPrimaryKey(String)}���ɿ��ٷ���key-value��ʽ��Map����
     */
    @MetaData(value = "��Ҫ����")
    @EntityAutoCode(order = 20, search = true)
    private String primaryValue;

    /**
     * �ֵ����ݶ�Ӧ�Ĳ�������Valueֵ���������primaryValueҵ�������Ҫ�����������ݿ�������չValue�ֶδ�ȡ��Щֵ
     * ������չ���ݵĻ�ȡһ��ͨ��{@link **.sys.service.DataDictService#findByPrimaryKey(String)}
     * ���ڷ��ص����ݣ�����ʵ��ҵ���ƻ�ʹ�ü���
     */
    @MetaData(value = "��Ҫ����")
    @EntityAutoCode(order = 30, search = true)
    private String secondaryValue;

    /**
     * �ֵ����ݶ�Ӧ�Ĳ������ݴ��ı�����Valueֵ���������primaryValueҵ�������Ҫ�����������ݿ�������չValue�ֶδ�ȡ��Щֵ
     * ������չ���ݵĻ�ȡһ��ͨ��{@link ***.sys.service.DataDictService#findByPrimaryKey(String)}
     * ���ڷ��ص����ݣ�����ʵ��ҵ���ƻ�ʹ�ü���
     */
    @MetaData(value = "���ı�����", tooltips = "��CLOB���ı���ʽ�洢�����ض��Ĵ��ı���������")
    @EntityAutoCode(order = 30, search = false)
    private String richTextValue;

    @MetaData(value = "���ñ�ʶ", tooltips = "������Ŀȫ�ֲ���ʾ")
    @EntityAutoCode(order = 40, search = true)
    private Boolean disabled = Boolean.FALSE;

    @MetaData(value = "�����", tooltips = "�������ţ�����Խ��Խ������ʾ")
    @EntityAutoCode(order = 1000)
    private Integer orderRank = 10;

    @MetaData(value = "���ڵ�")
    private DataDict parent;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(Integer orderRank) {
        this.orderRank = orderRank;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "PARENT_ID")
    @JsonProperty
    public DataDict getParent() {
        return parent;
    }

    public void setParent(DataDict parent) {
        this.parent = parent;
    }

    @Override
    @Transient
    public String getDisplay() {
        return primaryKey + ":" + primaryValue;
    }

    @Column(length = 128, nullable = false)
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Column(length = 128)
    public String getSecondaryKey() {
        return secondaryKey;
    }

    public void setSecondaryKey(String secondaryKey) {
        this.secondaryKey = secondaryKey;
    }

    public String getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(String primaryValue) {
        this.primaryValue = primaryValue;
    }

    public String getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(String secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    @Lob
    public String getRichTextValue() {
        return richTextValue;
    }

    public void setRichTextValue(String richTextValue) {
        this.richTextValue = richTextValue;
    }
}

