package com.etrade.framework.core.sys.entity;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/***
 * TODO delete this implements FileDef
 * 
 * @author kejun.song
 * @version $Id: AttachmentFile.java, v 0.1 2014��10��23�� ����7:16:03 kejun.song Exp $
 */
@Entity
@Table(name = "TBL_SYS_ATTACHMENT_FILE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "�����ļ�����")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class AttachmentFile extends BaseEntity<String> {

    /**  */
    private static final long serialVersionUID = 1L;

    /** �����ϴ��ļ����� */
    private String fileRealName;

    /** �ļ����� */
    private String fileDescription;

    /** ������չ�� */
    private String fileExtension;

    /** ������С */
    private int fileLength;

    /** ����MIME���� */
    private String fileType;

    private String fileRelativePath;

    private String entityClassName;

    @MetaData(value = "����ʵ������")
    private String entityId;
    
    @MetaData(value = "����", comments = "���һ�������ж�����͹�����������ͨ�������Խ��з���")
    private String entityFileCategory;

    private Date lastTouchTime;

    private String lastTouchBy;

    private String id;

    /** ֱ�����ļ�byte���ݼ����MD5���UUID��ΪΨһ��ʶ */
    @Override
    @Id
    @Column(length = 80)
    @JsonProperty
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    @Column(length = 500, nullable = false)
    @JsonProperty
    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }

    @Column(nullable = false)
    @JsonProperty
    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    @Column(length = 32, nullable = false)
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Column(length = 8)
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Column(length = 200, nullable = true)
    @JsonIgnore
    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @Override
    @Transient
    public String getDisplay() {
        return fileRealName;
    }

    @Column(length = 512, nullable = true)
    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    @Column(length = 200, nullable = true)
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Date getLastTouchTime() {
        return lastTouchTime;
    }

    public void setLastTouchTime(Date lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

    public String getLastTouchBy() {
        return lastTouchBy;
    }

    public void setLastTouchBy(String lastTouchBy) {
        this.lastTouchBy = lastTouchBy;
    }

    @Transient
    @JsonIgnore
    public static AttachmentFile buildInstance(File file) {
        //����������UUID��Ϊ������ÿ���ϴ����ᴴ���ļ���������ݼ�¼�����ڹ������Ǵ�����ͬ�ļ��ظ��������
        String id = UUID.randomUUID().toString();

        DateTime now = new DateTime();
        StringBuilder sb = new StringBuilder();
        int year = now.getYear();
        sb.append("/" + year);
        String month = "";
        int monthOfYear = now.getMonthOfYear();
        if (monthOfYear < 10) {
            month = "0" + monthOfYear;
        } else {
            month = "" + monthOfYear;
        }
        String day = "";
        int dayOfMonth = now.getDayOfMonth();
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = "" + dayOfMonth;
        }
        sb.append("/" + month);
        sb.append("/" + day);
        Assert.notNull(id, "id is required to buildInstance");
        sb.append("/" + StringUtils.substring(id, 0, 2));
        String path = sb.toString();

        AttachmentFile af = new AttachmentFile();
        af.setId(path.replaceAll("/", "") + id);
        af.setFileRelativePath(path);
        af.setFileLength((int) file.length());
        return af;
    }

    public String getFileRelativePath() {
        return fileRelativePath;
    }

    public void setFileRelativePath(String fileRelativePath) {
        this.fileRelativePath = fileRelativePath;
    }

    @Transient
    @JsonIgnore
    public String getDiskFileName() {
        return getId() + "." + getFileExtension();
    }

    public String getEntityFileCategory() {
        return entityFileCategory;
    }

    public void setEntityFileCategory(String entityFileCategory) {
        this.entityFileCategory = entityFileCategory;
    }
}
