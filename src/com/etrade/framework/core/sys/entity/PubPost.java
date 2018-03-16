package com.etrade.framework.core.sys.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.AttachmentableEntity;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "tbl_SYS_PUB_POST")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "����", comments = "������Ӧ�������û���ʾ�Ĺ�����Ϣ�������û���Ȩ�����ֿ���")
public class PubPost extends BaseUuidEntity implements AttachmentableEntity {

    private static final long serialVersionUID = 2544390748513253055L;

    @MetaData(value = "����")
    private String htmlTitle;

    @MetaData(value = "����ʱ��")
    private Date publishTime;

    @MetaData(value = "����ʱ��")
    private Date expireTime;

    @MetaData(value = "ǰ����ʾ")
    private Boolean frontendShow = Boolean.FALSE;

    @MetaData(value = "��̨��ʾ")
    private Boolean backendShow = Boolean.TRUE;

    @MetaData(value = "�ⲿ����")
    @EntityAutoCode(order = 40)
    private String externalLink;

    @MetaData(value = "��������")
    @EntityAutoCode(order = 45)
    private String htmlContent;

    @MetaData(value = "�ܼƲ鿴�û���")
    @EntityAutoCode(order = 50)
    private Integer readUserCount;

    @MetaData(value = "�����", tooltips = "����Խ����ʾԽ����")
    @EntityAutoCode(order = 50)
    private Integer orderRank = 100;

    @Override
    @Transient
    public String getDisplay() {
        return htmlTitle;
    }

    @Column(nullable = false)
    public String getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Lob
    @JsonIgnore
    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getReadUserCount() {
        return readUserCount;
    }

    public void setReadUserCount(Integer readUserCount) {
        this.readUserCount = readUserCount;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public Boolean getFrontendShow() {
        return frontendShow;
    }

    public void setFrontendShow(Boolean frontendShow) {
        this.frontendShow = frontendShow;
    }

    public Boolean getBackendShow() {
        return backendShow;
    }

    public void setBackendShow(Boolean backendShow) {
        this.backendShow = backendShow;
    }

    public Integer getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(Integer orderRank) {
        this.orderRank = orderRank;
    }

    @Transient
    public boolean isInternal() {
        return StringUtils.isBlank(externalLink);
    }
    
    @MetaData(value = "������������", comments = "�����б���ʾ�͹��������������ж�")
    private Integer attachmentSize;

    @Override
    public Integer getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(Integer attachmentSize) {
        this.attachmentSize = attachmentSize;
    }
}
