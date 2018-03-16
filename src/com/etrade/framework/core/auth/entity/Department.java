package com.etrade.framework.core.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;

@SuppressWarnings("serial")
@Entity
@Table(name = "TBL_AUTH_DEPARTMENT")
@MetaData(value = "����")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department extends BaseUuidEntity {

    @MetaData(value = "����")
    @EntityAutoCode(order = 5, listShow = true)
    private String code;

    @MetaData(value = "����")
    @EntityAutoCode(order = 10, listShow = true)
    private String title;

    @MetaData(value = "����")
    @EntityAutoCode
    private String description;

    @MetaData(value = "��ϵ�绰")
    @EntityAutoCode(order = 20, listShow = true)
    private String contactTel;

    @MetaData(value = "��������")
    @EntityAutoCode(order = 30, listShow = true)
    private User manager;

    @Column(nullable = false, length = 64)
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

    @Size(min = 3)
    @Column(nullable = false, length = 64, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    @Transient
    public String getDisplay() {
        return title;
    }

    @Column(nullable = true, length = 64)
    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    @OneToOne
    @JoinColumn(name = "USER_ID")
    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}
