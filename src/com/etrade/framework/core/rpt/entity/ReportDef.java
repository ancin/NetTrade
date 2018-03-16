package com.etrade.framework.core.rpt.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.etrade.framework.core.entity.annotation.EntityAutoCode;
import com.etrade.framework.core.sys.entity.AttachmentFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

@Entity
@Table(name = "tbl_RPT_REPORT_DEF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "������")
public class ReportDef extends BaseUuidEntity {

    /**  */
    private static final long     serialVersionUID = 1L;

    @MetaData(value = "����")
    @EntityAutoCode(order = 5, search = true)
    private String code;

    @MetaData(value = "����", tooltips = "�����ƻ���Ϊ���������ļ�����")
    @EntityAutoCode(order = 10, search = true)
    private String title;

    @MetaData(value = "����", tooltips = " ���ڱ����÷�������")
    @EntityAutoCode(order = 20, search = false, listShow = false)
    private String description;

    @MetaData(value = "����")
    @EntityAutoCode(order = 30, search = true)
    private ReportTypeEnum type;

    @MetaData(value = "����", tooltips = "���ڱ���ķ��࣬��������������ʾ")
    @EntityAutoCode(order = 40, search = true)
    private String category;

    @MetaData(value = "�����", tooltips = "�������б���ʾȷ���Ⱥ�˳��")
    @EntityAutoCode(order = 60, search = false)
    private Integer orderRank = 100;

    @MetaData(value = "���ñ�ʶ", tooltips = "����ȫ�ֲ���ʾ")
    @EntityAutoCode(order = 70, search = true)
    private Boolean disabled = Boolean.FALSE;

    @MetaData(value = "ģ���ļ�ID")
    @EntityAutoCode(order = 100, search = false)
    private AttachmentFile templateFile;

    @MetaData(value = "�����ı������", tooltips = "һ����Ҫ����JasperReport���ͱ���,JXLS����һ������ÿ��ҵ��Action�������ض���֯���������JXLS��������")
    private List<ReportParam> reportParameters;

    @MetaData(value = "��ɫ����")
    private List<ReportDefR2Role> reportDefR2Roles = Lists.newArrayList();
    
    public static enum ReportTypeEnum {

        /** 
         * ����JXLS���ƻ�Excel��� 
         * ��Excelģ����Ƕ��JXLS�ű����룬Ȼ���ڸ�ҵ��Action��������֯��Ĳ�������JXLS������
         * Ȼ����JXLS����ת�����������Excel�ļ�
         */
        @MetaData(value = "Excel(JXLS)")
        EXCEL_JAVA,

        /** 
         * ����VBS��Excelģ�壬һ������Excel��ֱ��Ƕ�뱨�����߼����ṩ�û����ش�Excel������Excel�������ɱ���
         * ��ģʽ�����Ƕ�����Ӧ��ʵ�ֵı�����ʽ����ȫ�����¿���VBS�ű�Excel�ļ����� 
         */
        @MetaData(value = "Excel(VBS)")
        EXCEL_VBS,

        /** 
         * ����iReport��Ƶ�JasperReportģ���ļ�
         * һ���ṩ��iReport������ɵ�.jasper�ļ���Ϊģ���ļ��ṩ��JasperReport API��������
         */
        @MetaData(value = "JasperReport")
        JASPER;
    }

    @Override
    @Transient
    public String getDisplay() {
        return title;
    }

    @Column(nullable = false, unique = true, length = 64)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 128, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 1024, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    public ReportTypeEnum getType() {
        return type;
    }

    public void setType(ReportTypeEnum type) {
        this.type = type;
    }

    @Column(length = 128, nullable = false)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(Integer orderRank) {
        this.orderRank = orderRank;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @OneToMany(mappedBy = "reportDef", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy(value = "orderRank desc")
    public List<ReportParam> getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(List<ReportParam> reportParameters) {
        this.reportParameters = reportParameters;
    }

    @OneToMany(mappedBy = "reportDef", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<ReportDefR2Role> getReportDefR2Roles() {
        return reportDefR2Roles;
    }

    public void setReportDefR2Roles(List<ReportDefR2Role> reportDefR2Roles) {
        this.reportDefR2Roles = reportDefR2Roles;
    }

    @OneToOne
    @JoinColumn(name = "FILE_ID")
    public AttachmentFile getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(AttachmentFile templateFile) {
        this.templateFile = templateFile;
    }

    @Transient
    @JsonIgnore
    public String getReportAccessUrl() {
        return "/rpt/jasper-report!show?report=" + code;
    }
}
