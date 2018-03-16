package com.etrade.biz.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@MetaData("��Ʒ�¼��ϸ��")
@Entity
@Table(name = "myt_finance_account_in_out")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class AccountInOut extends BaseBizEntity {

    /**  */
    private static final long serialVersionUID = 1L;
    @MetaData("ƾ֤��")
    private String voucher;
    @MetaData(value = "��ƾ֤��", comments = "�����۵������")
    private String subVoucher;
    @MetaData("��Դƾ֤����")
    private VoucherTypeEnum voucherType;
    @MetaData("������ƿ�Ŀ")
    private AccountSubject accountSubject;
    @MetaData(value = "��ƿ�Ŀ����", comments = "�������ԣ���ҵ�����˵���accountSubject����")
    private String accountSubjectCode;
    @MetaData(value = "������˷���", comments = "true=�裬false=��")
    private Boolean accountDirection;
    @MetaData(value = "����ժҪ")
    private String accountSummary;
    @MetaData("������λ")
    private BizTradeUnit bizTradeUnit;
    @MetaData("���")
    private BigDecimal amount;

    @MetaData("��¼����")
    private Date documentDate = new Date();

    @MetaData("�������� ")
    private Date postingDate = new Date();

    @MetaData("��ע")
    private String adminMemo;

    @MetaData("��Ʒ�¼��ϸ��")
    private BigDecimal directionAmount;

    @MetaData(value = "��ʶ�����Ѻ��")
    private Boolean redword;

    @Column(length = 128, nullable = false)
    @JsonProperty
    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    @Column(length = 128, nullable = true)
    @JsonProperty
    public String getSubVoucher() {
        return subVoucher;
    }

    public void setSubVoucher(String subVoucher) {
        this.subVoucher = subVoucher;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    @JsonProperty
    public VoucherTypeEnum getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(VoucherTypeEnum voucherType) {
        this.voucherType = voucherType;
    }

    @Column(nullable = false)
    @JsonProperty
    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    @Column(nullable = false)
    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    @Lob
    public String getAdminMemo() {
        return adminMemo;
    }

    public void setAdminMemo(String adminMemo) {
        this.adminMemo = adminMemo;
    }

    @ManyToOne
    @JoinColumn(name = "account_subject_id", nullable = false)
    @JsonProperty
    public AccountSubject getAccountSubject() {
        return accountSubject;
    }

    public void setAccountSubject(AccountSubject accountSubject) {
        this.accountSubject = accountSubject;
    }

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "biz_trade_unit_id", nullable = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonProperty
    @ForeignKey(name = "none")
    public BizTradeUnit getBizTradeUnit() {
        return bizTradeUnit;
    }

    public void setBizTradeUnit(BizTradeUnit bizTradeUnit) {
        this.bizTradeUnit = bizTradeUnit;
    }

    @Column(length = 32, nullable = false)
    public String getAccountSubjectCode() {
        return accountSubjectCode;
    }

    public void setAccountSubjectCode(String accountSubjectCode) {
        this.accountSubjectCode = accountSubjectCode;
    }

    @JsonProperty
    public Boolean getAccountDirection() {
        return accountDirection;
    }

    public void setAccountDirection(Boolean accountDirection) {
        this.accountDirection = accountDirection;
    }

    @Column(length = 1000)
    @JsonProperty
    public String getAccountSummary() {
        return accountSummary;
    }

    public void setAccountSummary(String accountSummary) {
        this.accountSummary = accountSummary;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getDirectionAmount() {
        return directionAmount;
    }

    public void setDirectionAmount(BigDecimal directionAmount) {
        this.directionAmount = directionAmount;
    }

    public Boolean getRedword() {
        return redword;
    }

    public void setRedword(Boolean redword) {
        this.redword = redword;
    }
}