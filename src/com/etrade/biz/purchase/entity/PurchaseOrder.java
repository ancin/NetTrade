package com.etrade.biz.purchase.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.biz.finance.entity.AccountSubject;
import com.etrade.biz.finance.entity.BizTradeUnit;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.bpm.BpmTrackable;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.etrade.framework.core.web.json.DateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@MetaData("�ɹ�����")
@Entity
@Table(name = "myt_purchase_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class PurchaseOrder extends BaseBizEntity implements BpmTrackable {

    private static final long serialVersionUID = -6347747732868248940L;
    
    @MetaData("Ψһƾ֤��")
    private String voucher;
    @MetaData("ƾ֤����")
    private Date voucherDate;
    @MetaData("������")
    private User voucherUser;
    @MetaData("���첿��")
    private Department voucherDepartment;
    @MetaData("������λ")
    private BizTradeUnit bizTradeUnit;

    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<PurchaseOrderDetail>();
    @MetaData(value = "����ժҪ")
    private String title;
    @MetaData("�ɹ���ע")
    private String adminMemo;
    @MetaData("ϵͳ��ע")
    private String sysMemo;
    @MetaData("����״̬")
    private PurchaseOrderStatusEnum orderStatus = PurchaseOrderStatusEnum.S10N;

    @MetaData("�����ܽ��")
    private BigDecimal totalAmount;

    @MetaData("�Ѹ��ܽ��")
    private BigDecimal actualPayedAmount;

    @MetaData(value = "Ԥ�����ƿ�Ŀ")
    private AccountSubject accountSubject;
    @MetaData(value = "����ժҪ")
    private String accountSummary;
    @MetaData("��ڵ�����")
    private String activeTaskName;
    @MetaData("��������")
    private PurchaseOrderPayModeEnum payMode = PurchaseOrderPayModeEnum.PREV;
    @MetaData(value = "��������ƾ֤���б�", comments = "���ŷָ���һ�����ڼ�¼��������ϵͳ�����ƾ֤��Ϣ")
    private String paymentVouchers;
    @MetaData(value = "����ο���Ϣ", comments = "һ�����ڼ�¼��������ϵͳ�����ƾ֤��Ϣ")
    private String paymentReference;

    @MetaData("�ۺ�Ӧ���ܽ��")
    private BigDecimal amount;

    @MetaData("��˰�ܽ��")
    private BigDecimal totalTaxAmount;

    @MetaData("�����Żݽ��")
    private BigDecimal totalDiscountAmount;

    @MetaData("�����˷�")
    private BigDecimal totalDeliveryAmount;
    @MetaData("����ʱ��")
    private Date deliveryTime;

    @MetaData("��ݹ�˾")
    private BizTradeUnit logistics;

    @MetaData("��ݵ���")
    private String logisticsNo;

    @MetaData("�ύʱ��")
    private Date submitDate;
    @MetaData("���ʱ��")
    private Date auditDate;
    @MetaData("���ʱ��")
    private Date redwordDate;
    @MetaData(value = "�������ժҪ")
    private String lastOperationSummary;

    public enum PurchaseOrderStatusEnum {

        @MetaData("���ύ")
        S10N,

        @MetaData("�ύ����")
        S20S,

        @MetaData("���ͨ��")
        S30AP,

        @MetaData("���δ��")
        S40ANP,

        @MetaData("���µ�")
        S50EX,

        @MetaData("�����ջ�")
        S60RECVS,

        @MetaData("����ջ�")
        S65RECVE,

        @MetaData("��ɸ���")
        S75FIE,

        @MetaData("�ѹر�")
        S80C,

        @MetaData("��ȡ��")
        S90CNC;

    }

    public enum PurchaseOrderPayModeEnum {

        @MetaData("Ԥ��")
        PREV,

        @MetaData("����")
        POST;

    }

    @Column(length = 128, nullable = false, unique = true, updatable = false)
    @JsonProperty
    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    @Column(nullable = false)
    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "voucher_user_id", nullable = false)
    @JsonProperty
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    public User getVoucherUser() {
        return voucherUser;
    }

    public void setVoucherUser(User voucherUser) {
        this.voucherUser = voucherUser;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "voucher_department_id", nullable = false)
    @JsonProperty
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    public Department getVoucherDepartment() {
        return voucherDepartment;
    }

    public void setVoucherDepartment(Department voucherDepartment) {
        this.voucherDepartment = voucherDepartment;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    @JsonProperty
    public PurchaseOrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(PurchaseOrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }

    @Column(length = 1024)
    @JsonProperty
    public String getAdminMemo() {
        return adminMemo;
    }

    public void setAdminMemo(String adminMemo) {
        this.adminMemo = adminMemo;
    }

    @Column(length = 128)
    public String getSysMemo() {
        return sysMemo;
    }

    public void setSysMemo(String sysMemo) {
        this.sysMemo = sysMemo;
    }

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "biz_trade_unit_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonProperty
    public BizTradeUnit getBizTradeUnit() {
        return bizTradeUnit;
    }

    public void setBizTradeUnit(BizTradeUnit bizTradeUnit) {
        this.bizTradeUnit = bizTradeUnit;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getActualPayedAmount() {
        return actualPayedAmount;
    }

    public void setActualPayedAmount(BigDecimal actualPayedAmount) {
        this.actualPayedAmount = actualPayedAmount;
    }

    @Override
    public String toString() {
        return "PurchaseOrder:  " + voucher;
    }

    @Override
    @JsonProperty
    public String getActiveTaskName() {
        return activeTaskName;
    }

    @Override
    public void setActiveTaskName(String activeTaskName) {
        this.activeTaskName = activeTaskName;
    }

    @JsonProperty
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = false)
    @JsonProperty
    public PurchaseOrderPayModeEnum getPayMode() {
        return payMode;
    }

    public void setPayMode(PurchaseOrderPayModeEnum payMode) {
        this.payMode = payMode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getTotalDeliveryAmount() {
        return totalDeliveryAmount;
    }

    public void setTotalDeliveryAmount(BigDecimal totalDeliveryAmount) {
        this.totalDeliveryAmount = totalDeliveryAmount;
    }

    @ManyToOne
    @JoinColumn(name = "account_subject_id")
    public AccountSubject getAccountSubject() {
        return accountSubject;
    }

    public void setAccountSubject(AccountSubject accountSubject) {
        this.accountSubject = accountSubject;
    }

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "logistics_id", nullable = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonProperty
    public BizTradeUnit getLogistics() {
        return logistics;
    }

    public void setLogistics(BizTradeUnit logistics) {
        this.logistics = logistics;
    }

    @Column(length = 32)
    @JsonProperty
    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    @JsonProperty
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    @Transient
    @JsonProperty
    public String getDisplay() {
        return voucher;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getBpmBusinessKey() {
        return voucher;
    }

    public String getAccountSummary() {
        return accountSummary;
    }

    public void setAccountSummary(String accountSummary) {
        this.accountSummary = accountSummary;
    }

    public Date getRedwordDate() {
        return redwordDate;
    }

    @SkipParamBind
    public void setRedwordDate(Date redwordDate) {
        this.redwordDate = redwordDate;
    }

    @JsonProperty
    public String getLastOperationSummary() {
        return lastOperationSummary;
    }

    public void setLastOperationSummary(String lastOperationSummary) {
        this.lastOperationSummary = lastOperationSummary;
    }

    @Column(length = 2000)
    @JsonIgnore
    public String getPaymentVouchers() {
        return paymentVouchers;
    }

    public void setPaymentVouchers(String paymentVouchers) {
        this.paymentVouchers = paymentVouchers;
    }

    @Lob
    @JsonIgnore
    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    @SkipParamBind
    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    @JsonProperty
    public Date getSubmitDate() {
        return submitDate;
    }

    @SkipParamBind
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Column(length = 2000)
    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}