package com.etrade.framework.core.auth.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("serial")
@Entity
@Table(name = "TBL_AUTH_SIGNUP_USER")
@MetaData(value = "����ע���˺�����")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class SignupUser extends BaseUuidEntity {

    @MetaData(value = "�û�Ψһ��ʶ��")
    private String uid;

    @MetaData(value = "��¼�˺�")
    @EntityAutoCode(order = 10, search = true)
    private String signinid;

    @MetaData(value = "��¼����")
    @EntityAutoCode(order = 10, listShow = false)
    private String password;

    @MetaData(value = "�ǳ�")
    @EntityAutoCode(order = 20, search = true)
    private String nick;

    @MetaData(value = "�����ʼ�")
    @EntityAutoCode(order = 30, search = true)
    private String email;

    @MetaData(value = "ע��ʱ��")
    @EntityAutoCode(order = 40, edit = false, listHidden = true)
    private Date signupTime;

    @MetaData(value = "��ϵ��Ϣ")
    @EntityAutoCode
    private String contactInfo;

    @MetaData(value = "��ע˵��")
    @EntityAutoCode
    private String remarkInfo;

    @MetaData(value = "��˴���ʱ��")
    private Date auditTime;

    @Size(min = 3, max = 30)
    @Column(length = 128, unique = true, nullable = false)
    public String getSigninid() {
        return signinid;
    }

    public void setSigninid(String signinid) {
        this.signinid = signinid;
    }

    @Column(updatable = false, length = 128, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(length = 64)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getSignupTime() {
        return signupTime;
    }

    @SkipParamBind
    public void setSignupTime(Date signupTime) {
        this.signupTime = signupTime;
    }

    @Email
    @Column(length = 128)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @Transient
    public String getDisplay() {
        return signinid;
    }

    @Column(length = 3000)
    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Column(length = 3000)
    public String getRemarkInfo() {
        return remarkInfo;
    }

    public void setRemarkInfo(String remarkInfo) {
        this.remarkInfo = remarkInfo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @Column(updatable = false, length = 64, unique = true, nullable = false)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
