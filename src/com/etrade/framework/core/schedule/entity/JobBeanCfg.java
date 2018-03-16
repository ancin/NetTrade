package com.etrade.framework.core.schedule.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;

@MetaData(value = "��ʱ��������")
@Entity
@Table(name = "tbl_JOB_BEAN_CFG")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JobBeanCfg extends BaseUuidEntity {

    @MetaData(value = "������ȫ��", tooltips = "ʵ��QuartzJobBean����ȫ·������ ")
    @EntityAutoCode(order = 5)
    private String jobClass;

    @MetaData(value = "CRON���ʽ", tooltips = "��ʱ���ʽ������CRON�﷨")
    @EntityAutoCode(order = 10)
    private String cronExpression;

    @MetaData(value = "�Զ���ʼ����")
    @EntityAutoCode(order = 20)
    private Boolean autoStartup = Boolean.TRUE;

    @MetaData(value = "�������м�¼", tooltips = "ÿ�����л�д����ʷ��¼����������Ƶ�ʺܸ߻�ҵ�������岻���������ر�")
    @EntityAutoCode(order = 30)
    private Boolean logRunHist = Boolean.TRUE;

    @MetaData(value = "��Ⱥ����ģʽ", tooltips = "���Ϊtrue������һ�鼯Ⱥ���𻷾���ͬһ����ֻ����һ���ڵ㴥��<br/>������ÿ���ڵ���Զ�������")
    @EntityAutoCode(order = 40)
    private Boolean runWithinCluster = Boolean.TRUE;

    @MetaData(value = "����")
    @EntityAutoCode(order = 50, listShow = false)
    private String description;

    @MetaData(value = "���ģ���ı�")
    private String resultTemplate;

    @Column(length = 128, nullable = false, unique = true)
    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    @Column(length = 64, nullable = false)
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Column(length = 1000, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public Boolean getAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    @Transient
    public String getDisplay() {
        return jobClass + ":" + cronExpression;
    }

    @Column(nullable = false)
    public Boolean getLogRunHist() {
        return logRunHist;
    }

    public void setLogRunHist(Boolean logRunHist) {
        this.logRunHist = logRunHist;
    }

    @Column(nullable = false)
    public Boolean getRunWithinCluster() {
        return runWithinCluster;
    }

    public void setRunWithinCluster(Boolean runWithinCluster) {
        this.runWithinCluster = runWithinCluster;
    }

    @Lob
    public String getResultTemplate() {
        return resultTemplate;
    }

    public void setResultTemplate(String resultTemplate) {
        this.resultTemplate = resultTemplate;
    }
}
