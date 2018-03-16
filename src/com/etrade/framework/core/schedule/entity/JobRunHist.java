package com.etrade.framework.core.schedule.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;

/**
 * ����ƻ�������ʷ��¼
 */
@MetaData(value = "��ʱ�������м�¼")
@Entity
@Table(name = "tbl_JOB_RUN_HIST")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JobRunHist extends BaseUuidEntity {

    @MetaData(value = "Job����")
    @EntityAutoCode(order = 10, listShow = false)
    private String jobName;

    @MetaData(value = "Job����")
    @EntityAutoCode(order = 20, listShow = false)
    private String jobGroup;

    @MetaData(value = "Job��")
    @EntityAutoCode(order = 25)
    private String jobClass;

    @MetaData(value = "Trigger����")
    @EntityAutoCode(order = 30, listShow = false)
    private String triggerName;

    @MetaData(value = "Trigger���� ")
    @EntityAutoCode(order = 40, listShow = false)
    private String triggerGroup;
    
    @MetaData(value = "�쳣��ʶ")
    @EntityAutoCode(order = 90)
    private Boolean exceptionFlag = Boolean.FALSE;

    @MetaData(value = "ִ�н��")
    @EntityAutoCode(order = 100)
    private String result;

    @MetaData(value = "�쳣��־")
    @EntityAutoCode(order = 110, listShow = false)
    private String exceptionStack;

    //���²�������ο��ٷ��ӿ��ĵ�˵����
    //org.quartz.plugins.history.LoggingJobHistoryPlugin.LoggingJobHistoryPlugin#jobWasExecuted(JobExecutionContext context, JobExecutionException jobException)
    @MetaData(value = "���δ���ʱ��")
    @EntityAutoCode(order = 50)
    private Date fireTime;

    @EntityAutoCode(order = 60)
    @MetaData(value = "�ϴδ���ʱ��")
    private Date previousFireTime;

    @EntityAutoCode(order = 70)
    @MetaData(value = "�´δ���ʱ��")
    private Date nextFireTime;

    @MetaData(value = "��������")
    @EntityAutoCode(order = 80)
    private Integer refireCount;
    
    @MetaData(value = "�����ڵ��ʶ")
    @EntityAutoCode(order = 100)
    private String nodeId;

    @Column(length = 64, nullable = true)
    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Column(length = 64, nullable = true)
    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    @Column(length = 64, nullable = true)
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Column(length = 64, nullable = true)
    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public void setFireTime(Date fireTime) {
        this.fireTime = fireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Integer getRefireCount() {
        return refireCount;
    }

    public void setRefireCount(Integer refireCount) {
        this.refireCount = refireCount;
    }

    @Type(type = "yes_no")
    @Column(nullable = true)
    public Boolean getExceptionFlag() {
        return exceptionFlag;
    }

    public void setExceptionFlag(Boolean exceptionFlag) {
        this.exceptionFlag = exceptionFlag;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public String getExceptionStack() {
        return exceptionStack;
    }

    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }

    @Override
    @Transient
    public String getDisplay() {
        return jobClass;
    }

    @Column(length = 512, nullable = true)
    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
