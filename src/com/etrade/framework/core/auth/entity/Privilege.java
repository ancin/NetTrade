package com.etrade.framework.core.auth.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

/**
 * Ȩ�޿�����Դ��ָ�Բ˵�����ť��URL�ȷ��ʿ��Ƶ�
 * 
 */
@Entity
@Table(name = "TBL_AUTH_PRIVILEGE")
@MetaData(value = "Ȩ��")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Privilege extends BaseUuidEntity {

    /**  */
    private static final long     serialVersionUID         = 1L;

    public final static String DATA_DICT_PRIVILEGE_TYPE = "PRIVILEGE_TYPE";

    @MetaData(value = "����")
    @EntityAutoCode(order = 5, search = true)
    private String category;

    @MetaData(value = "����")
    @EntityAutoCode(order = 8, search = true)
    private PrivilegeTypeEnum type = PrivilegeTypeEnum.URL;

    @MetaData(value = "����", comments = "����Ȩ�ޱ�ǩ��S2PrivilegeTag.java")
    @EntityAutoCode(order = 10, search = true)
    private String code;

    @MetaData(value = "����")
    @EntityAutoCode(order = 20, search = true)
    private String title;

    @MetaData(value = "URL", tooltips = "һ��Ȩ�޹������URL,һ��һ��URL<br/>ϵͳĬ�ϰ�����ƥ�������֤����/abc��ʾ/abc**")
    @EntityAutoCode(order = 30, search = true)
    private String url;

    @MetaData(value = "����")
    @EntityAutoCode(listShow = false)
    private String description;

    @MetaData(value = "���ñ�ʶ", tooltips = "���ò�����Ȩ�޿����߼�")
    @EntityAutoCode(order = 40, search = true)
    private Boolean disabled = Boolean.FALSE;

    /** 
     * Ȩ�޿��Ƶ����ȼ�������Խ��ƥ�����ȼ�Խ�ߣ��󲿷��������Ҫ���Ǵ����ԣ�ֱ��Ĭ��ֵ���ɣ�
     * ���Ȩ��URL�����ɰ�����ϵ��Ҫ�õ������ԣ�������鿴Spring Security����URLƥ�����˵�� 
     */
    @MetaData(value = "�����", tooltips = "Ȩ�޿��Ƶ����ȼ�������Խ��ƥ�����ȼ�Խ��")
    @EntityAutoCode(order = 50)
    private Integer orderRank = 100;

    @MetaData(value = "��ɫȨ�޹���")
    private List<RoleR2Privilege> roleR2Privileges = Lists.newArrayList();

    public static enum PrivilegeTypeEnum {

        @MetaData(value = "URL")
        URL,

        @MetaData(value = "��ť")
        BTN,

        @MetaData(value = "�˵�")
        MENU;

    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 128)
    public PrivilegeTypeEnum getType() {
        return type;
    }

    public void setType(PrivilegeTypeEnum type) {
        this.type = type;
    }

    @Column(nullable = false, length = 256)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(nullable = true, length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false, length = 64, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    @Column(nullable = true, length = 512)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(nullable = false, length = 128)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    @Transient
    public String getDisplay() {
        return title;
    }

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<RoleR2Privilege> getRoleR2Privileges() {
        return roleR2Privileges;
    }

    public void setRoleR2Privileges(List<RoleR2Privilege> roleR2Privileges) {
        this.roleR2Privileges = roleR2Privileges;
    }
}