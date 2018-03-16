package com.etrade.framework.core.sys.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_SYS_PUB_POST_READ", uniqueConstraints = @UniqueConstraint(columnNames = { "pub_post_id",
        "read_user_id" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@MetaData(value = "公告阅读记录")
public class PubPostRead extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    @MetaData(value = "公告")
    @EntityAutoCode(order = 10)
    private PubPost pubPost;

    @MetaData(value = "阅读用户")
    @EntityAutoCode(order = 40)
    private User readUser;

    @MetaData(value = "首次阅读时间")
    @EntityAutoCode(order = 30)
    private Date firstReadTime;

    @MetaData(value = "最后阅读时间")
    @EntityAutoCode(order = 35)
    private Date lastReadTime;

    @MetaData(value = "总计阅读次数")
    @EntityAutoCode(order = 40)
    private Integer readTotalCount = 1;

    @Override
    @Transient
    public String getDisplay() {
        return null;
    }

    @ManyToOne
    @JoinColumn(name = "pub_post_id", nullable = false)
    @JsonIgnore
    public PubPost getPubPost() {
        return pubPost;
    }

    public void setPubPost(PubPost pubPost) {
        this.pubPost = pubPost;
    }

    @ManyToOne
    @JoinColumn(name = "read_user_id", nullable = false)
    @JsonIgnore
    public User getReadUser() {
        return readUser;
    }

    public void setReadUser(User readUser) {
        this.readUser = readUser;
    }

    @Transient
    public String getReadUserLabel() {
        return readUser.getDisplay();
    }

    @Column(nullable = false)
    public Date getFirstReadTime() {
        return firstReadTime;
    }

    public void setFirstReadTime(Date firstReadTime) {
        this.firstReadTime = firstReadTime;
    }

    public Date getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Column(nullable = false)
    public Integer getReadTotalCount() {
        return readTotalCount;
    }

    public void setReadTotalCount(Integer readTotalCount) {
        this.readTotalCount = readTotalCount;
    }
}
