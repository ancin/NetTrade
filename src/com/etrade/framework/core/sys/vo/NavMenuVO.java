package com.etrade.framework.core.sys.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class MenuVO.
 */
public class NavMenuVO {

    private String id;
    
    /** �˵����룬����ǰ��JS���� */
    private String code;

    /** �˵����ƣ��˵���ʾ������ֵ. */
    private String name;

    /** ͼ�꣬����Bootstrap3����fa-user */
    private String icon;

    /**
     * �˵�URL.
     */
    private String url = "";

    /** ���ڵ�. */
    private NavMenuVO parent;

    /** ���ӽڵ�. */
    private List<NavMenuVO> children = new ArrayList<NavMenuVO>();

    /** �Ƿ�Ĭ��չ���˵��� */
    private Boolean open = Boolean.FALSE;

    /** ��ʾ��ʶ */
    private Boolean show = Boolean.FALSE;

    public void setShow(Boolean show) {
        this.show = show;
        if (show == true & this.getParent() != null) {
            this.getParent().setShow(true);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NavMenuVO getParent() {
        return parent;
    }

    public void setParent(NavMenuVO parent) {
        this.parent = parent;
    }

    public List<NavMenuVO> getChildren() {
        return children;
    }

    public void setChildren(List<NavMenuVO> children) {
        this.children = children;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getShow() {
        return show;
    }

    public int getChildrenSize() {
        return children == null ? 0 : children.size();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isHasIcon() {
        return StringUtils.isNotBlank(icon);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}