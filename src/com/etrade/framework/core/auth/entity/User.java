package com.etrade.framework.core.auth.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Email;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

@Entity
@Table(name = "T_AUTH_USER")
@MetaData(value = "�û�")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class User extends BaseEntity<Long> {

    /**  */
    private static final long    serialVersionUID     = 1L;

    public final static String[] PROTECTED_USER_NAMES = new String[] { "admin", "super" };

    @MetaData(value = "��������")
    private Department department;

    @MetaData(value = "��¼�˺�")
    @EntityAutoCode(order = 10, search = true)
    private String signinid;

    @MetaData(value = "��¼����")
    @EntityAutoCode(order = 10, listShow = false)
    private String password;

    @MetaData(value = "�ǳ�")
    @EntityAutoCode(order = 20, search = true)
    private String nick;

    @MetaData(value = "�ƶ��绰")
    private String mobilePhone;

    @MetaData(value = "�����ʼ�", tooltips = "�������û������һ����룬����ϵͳ֪ͨ��")
    private String email;

    @MetaData(value = "���ñ�ʶ", tooltips = "����֮�����ܵ�¼����ϵͳ")
    @EntityAutoCode(order = 40, search = true)
    private Boolean enabled = Boolean.TRUE;

    @MetaData(value = "ע��ʱ��")
    @EntityAutoCode(order = 40, edit = false, listHidden = true)
    private Date signupTime;

    @MetaData(value = "�˻�δ������־")
    @EntityAutoCode(order = 50, search = false, listHidden = true)
    private Boolean accountNonLocked = Boolean.TRUE;

    @MetaData(value = "ʧЧ����", tooltips = "�趨�˺ŷ���ϵͳ��ʧЧ���ڣ�Ϊ�ձ�ʾ����ʧЧ")
    @EntityAutoCode(order = 50, search = true)
    private Date accountExpireTime;

    @MetaData(value = "�˻��������ʱ��")
    @EntityAutoCode(order = 50, search = false, listHidden = true)
    private Date credentialsExpireTime;

    @MetaData(value = "��ɫ����")
    private List<UserR2Role> userR2Roles = Lists.newArrayList();

    @MetaData(value = "�û�Ψһ��ʶ��")
    private String uid;

    @MetaData(value = "����¼ʱ��")
    private Date lastLogonTime;

    @MetaData(value = "����¼IP")
    private String lastLogonIP;

    @MetaData(value = "����¼������")
    private String lastLogonHost;

    @MetaData(value = "�ܼƵ�¼����")
    private Integer logonTimes;

    @MetaData(value = "�ܼ���֤ʧ�ܴ���", comments = "��֤ʧ���ۼӣ��ɹ������㡣�ﵽ�趨ʧ�ܴ����������ʺţ���ֹ�����ƴ������Բ²�����")
    private Integer logonFailureTimes;

    @MetaData(value = "�����֤ʧ��ʱ��")
    private Date lastLogonFailureTime;

    @MetaData(value = "�����", comments = "�����һ������趨�����UUID�ַ���")
    private String randomCode;

    /** ������Ŀ���Զ��� */
    @Deprecated
    private String userPin;

    private Long id;

    @Override
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "native")
    @Column(name = "sid")
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    @Size(min = 3, max = 30)
    @Column(length = 128, unique = true, nullable = false, updatable = false, name = "user_id")
    public String getSigninid() {
        return signinid;
    }

    public void setSigninid(String signinid) {
        this.signinid = signinid;
    }

    @Column(length = 128)
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

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getAccountExpireTime() {
        return accountExpireTime;
    }

    public void setAccountExpireTime(Date accountExpireTime) {
        this.accountExpireTime = accountExpireTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getCredentialsExpireTime() {
        return credentialsExpireTime;
    }

    public void setCredentialsExpireTime(Date credentialsExpireTime) {
        this.credentialsExpireTime = credentialsExpireTime;
    }

    @Type(type = "yes_no")
    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    @Transient
    public String getDisplay() {
        return signinid;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @JsonIgnore
    public List<UserR2Role> getUserR2Roles() {
        return userR2Roles;
    }

    public void setUserR2Roles(List<UserR2Role> userR2Roles) {
        this.userR2Roles = userR2Roles;
    }

    @Column(updatable = false, length = 64, unique = true, nullable = false)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getLastLogonTime() {
        return lastLogonTime;
    }

    public void setLastLogonTime(Date lastLogonTime) {
        this.lastLogonTime = lastLogonTime;
    }

    @Column(length = 128, nullable = true)
    public String getLastLogonIP() {
        return lastLogonIP;
    }

    public void setLastLogonIP(String lastLogonIP) {
        this.lastLogonIP = lastLogonIP;
    }

    @Column(length = 128, nullable = true)
    public String getLastLogonHost() {
        return lastLogonHost;
    }

    public void setLastLogonHost(String lastLogonHost) {
        this.lastLogonHost = lastLogonHost;
    }

    public Integer getLogonTimes() {
        return logonTimes;
    }

    public void setLogonTimes(Integer logonTimes) {
        this.logonTimes = logonTimes;
    }

    @Type(type = "yes_no")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @NotAudited
    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(length = 50)
    public String getUserPin() {
        return userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public Integer getLogonFailureTimes() {
        return logonFailureTimes;
    }

    public void setLogonFailureTimes(Integer logonFailureTimes) {
        this.logonFailureTimes = logonFailureTimes;
    }

    public Date getLastLogonFailureTime() {
        return lastLogonFailureTime;
    }

    public void setLastLogonFailureTime(Date lastLogonFailureTime) {
        this.lastLogonFailureTime = lastLogonFailureTime;
    }

    @Column(length = 50)
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
