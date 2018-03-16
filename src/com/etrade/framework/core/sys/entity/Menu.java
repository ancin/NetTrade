package com.etrade.framework.core.sys.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_SYS_MENU", uniqueConstraints = @UniqueConstraint(columnNames = { "title", "PARENT_ID" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "�˵�")
public class Menu extends BaseUuidEntity implements Comparable<Menu> {

    /**  */
    private static final long  serialVersionUID = 1L;

    /** ���ڱ���˵������Ĺ̶��˵����� */
    public static final String MENU_CODE_RPT = "MFIXRPT";

    @MetaData(value = "����", comments = "�ɶ��ض���Ҫ���ʿ��ƵĲ˵������ô��룬Ĭ�Ͽ�Ϊ��")
    @EntityAutoCode(order = 10, search = true)
    private String code;

    @MetaData(value = "����")
    @EntityAutoCode(order = 20, search = true)
    private String title;

    @MetaData(value = "����")
    @EntityAutoCode(listShow = false)
    private String description;

    @MetaData(value = "���ñ�ʶ", tooltips = "���ò˵�ȫ�ֲ���ʾ")
    @EntityAutoCode(order = 40, search = true)
    private Boolean disabled = Boolean.FALSE;

    /**
     * @see #type
     */
    @MetaData(value = "�˵�URL")
    @EntityAutoCode(order = 30, search = true)
    private String url;

    @MetaData(value = "ͼ����ʽ")
    @EntityAutoCode(listHidden = true, order = 40)
    private String style;

    @MetaData(value = "����")
    @EntityAutoCode(order = 30, search = true)
    private MenuTypeEnum type = MenuTypeEnum.RELC;

    @MetaData(value = "�����", tooltips = "�������ţ�����Խ��Խ������ʾ")
    @EntityAutoCode(order = 1000)
    private Integer orderRank = 100;

    @MetaData(value = "չ����ʶ", tooltips = "�Ƿ�Ĭ��չ���˵���")
    @EntityAutoCode(order = 30, search = true)
    private Boolean initOpen = Boolean.FALSE;

    @MetaData(value = "���ڵ�")
    private Menu parent;

    @MetaData(value = "�ӽڵ㼯��")
    private List<Menu> children;

    @MetaData(value = "�ӽڵ���", comments = "�����ֶΣ���ǰ�ڵ������ӽڵ���Ŀ���������ڿ���ȷ����ǰ�Ƿ�Ҷ�ӽڵ�")
    private Integer childrenSize;

    @MetaData(value = "���ڲ㼶", comments = " �����ֶΣ���ǰ�ڵ����ڲ㼶�������Ч�����β㼶��ʾ")
    private Integer inheritLevel;

    public static enum MenuTypeEnum {

        @MetaData(value = "���������")
        RELC,

        @MetaData(value = "�������")
        RELD,

        @MetaData(value = "����·��")
        ABS;

    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "PARENT_ID")
    @JsonIgnore
    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent")
    @OrderBy("orderRank desc")
    @JsonIgnore
    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    @Column(length = 1000)
    @JsonIgnore
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 256)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    public MenuTypeEnum getType() {
        return type;
    }

    public void setType(MenuTypeEnum type) {
        this.type = type;
    }

    @Column(nullable = false)
    public Integer getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(Integer orderRank) {
        this.orderRank = orderRank;
    }

    public Boolean getInitOpen() {
        return initOpen;
    }

    public void setInitOpen(Boolean initOpen) {
        this.initOpen = initOpen;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getChildrenSize() {
        return childrenSize;
    }

    public void setChildrenSize(Integer childrenSize) {
        this.childrenSize = childrenSize;
    }

    public Integer getInheritLevel() {
        return inheritLevel;
    }

    public void setInheritLevel(Integer inheritLevel) {
        this.inheritLevel = inheritLevel;
    }

    @Column(length = 128)
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Column(length = 128, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    @Transient
    public String getDisplay() {
        return title;
    }

    @Override
    public int compareTo(Menu o) {
        return CompareToBuilder.reflectionCompare(o.getOrderRank(), this.getOrderRank());
    }

    @Column(nullable = false, length = 64, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * ����ڵ����ڲ㼶�����ڵ���0��ʼ
     * @return
     */
    @Transient
    @JsonIgnore
    public int getLevel() {
        int level = 0;
        return loopLevel(level, this);
    }

    private int loopLevel(int level, Menu menu) {
        Menu parent = menu.getParent();
        if (parent != null && parent.getId() != null) {
            return loopLevel(level + 1, menu.getParent());
        }
        return level;
    }
}
