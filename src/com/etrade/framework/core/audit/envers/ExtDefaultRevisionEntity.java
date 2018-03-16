package com.etrade.framework.core.audit.envers;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * ��չĬ�ϵ�Hibernate Envers��Ʊ������
 * 
 * @see http://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html/ch15.html
 */
@Entity
@Table(name = "T_AUD_REVINFO")
@RevisionEntity(ExtRevisionListener.class)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "javassistLazyInitializer", "revisionEntity", "handler" }, ignoreUnknown = true)
public class ExtDefaultRevisionEntity {
    /** ��¼�汾 */
    private Long rev;
    /** ��¼ʱ�� */
    private Date revstmp;
    /** ��¼�û��˺���Ϣ��һ������ҳ���Ѻ���ʾ */
    private String username;
    /** ȫ��Ψһ���û�ID��ȷ����ȷ��Ψһ�����û����� */
    private String uid;
    /** ����ThreadLocal��ʽ��¼ǰ��Web�����û���д�Ĳ���˵�� */
    private String operationExplain;
    /** ����ThreadLocal��ʽ��¼�����¼���¼ */
    private String operationEvent;
    /** ����ThreadLocal��ʽ��¼�����״̬ */
    private String oldState;
    /** ����ThreadLocal��ʽ��¼������״̬ */
    private String newState;

    /** ��������:��ʾ���ݶ�Ӧ���������ı� */
    private String operationEventDisplay;
    /** ��������:��ʾ���ݶ�Ӧ���������ı� */
    private String oldStateDisplay;
    /** ��������:��ʾ���ݶ�Ӧ���������ı� */
    private String newStateDisplay;

    @Id
    @GeneratedValue
    @RevisionNumber
    public Long getRev() {
        return rev;
    }

    public void setRev(Long rev) {
        this.rev = rev;
    }

    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getRevstmp() {
        return revstmp;
    }

    public void setRevstmp(Date revstmp) {
        this.revstmp = revstmp;
    }

    @Column(length = 128, name = "user_id")
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Column(length = 50, name = "user_name")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(length = 512)
    public String getOperationExplain() {
        return operationExplain;
    }

    public void setOperationExplain(String operationExplain) {
        this.operationExplain = operationExplain;
    }

    @Column(length = 128)
    public String getOperationEvent() {
        return operationEvent;
    }

    public void setOperationEvent(String operationEvent) {
        this.operationEvent = operationEvent;
    }

    @Column(length = 32)
    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    @Column(length = 32)
    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    @Transient
    public String getOperationEventDisplay() {
        return operationEventDisplay;
    }

    public void setOperationEventDisplay(String operationEventDisplay) {
        this.operationEventDisplay = operationEventDisplay;
    }

    @Transient
    public String getOldStateDisplay() {
        return oldStateDisplay;
    }

    public void setOldStateDisplay(String oldStateDisplay) {
        this.oldStateDisplay = oldStateDisplay;
    }

    @Transient
    public String getNewStateDisplay() {
        return newStateDisplay;
    }

    public void setNewStateDisplay(String newStateDisplay) {
        this.newStateDisplay = newStateDisplay;
    }
}
