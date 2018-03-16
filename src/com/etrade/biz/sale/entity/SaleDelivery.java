package com.etrade.biz.sale.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.etrade.biz.core.constant.VoucherStateEnum;
import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.biz.finance.entity.AccountSubject;
import com.etrade.biz.finance.entity.BizTradeUnit;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@MetaData(value = "����(����)��")
@Entity
@Table(name = "biz_sale_delivery")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SaleDelivery extends BaseBizEntity {
    /**  */
    private static final long        serialVersionUID = 1L;
    @MetaData("Ψһƾ֤��")
    private String voucher;
    @MetaData("ƾ֤����")
    private Date voucherDate;
    @MetaData("ƾ֤״̬")
    private VoucherStateEnum voucherState = VoucherStateEnum.DRAFT;
    @MetaData("������")
    private User voucherUser;
    @MetaData("���첿��")
    private Department voucherDepartment;

    @MetaData(value = "�ο���Դ", comments = "����д�������Ա�����è�ȱ�ʶ������Դ����Ϣ")
    private String referenceSource;
    @MetaData(value = "�ο�ƾ֤��", comments = "�義�����Ա�����è�ȶ����ŵ�")
    private String referenceVoucher;

    @MetaData(value = "����")
    private String title;
    @MetaData(value = "���ۿͻ�")
    private BizTradeUnit customerProfile;
    @MetaData(value = "���۱�ע")
    private String memo;

    @MetaData(value = "��Ʒ�ܳɱ�", comments = "���������Ʒ�ܳɱ�������������ͳ��")
    private BigDecimal commodityCostAmount;
    @MetaData(value = "�ۺ���Ʒ�ܽ��", comments = "���������Ʒ���۽�����������ͳ��")
    private BigDecimal commodityAmount;

    @MetaData(value = "��ȡ�ͻ��˷�")
    private BigDecimal chargeLogisticsAmount;
    @MetaData(value = "ʵ�ʷ����˷�")
    private BigDecimal logisticsAmount;
    @MetaData(value = "������ݹ�˾", comments = "Ӧ���˿����")
    private BizTradeUnit logistics;

    @MetaData(value = "ָ���ۼ��ܽ��", comments = "δ�ۿ�֮ǰԭʼָ���ۼ��ܽ��")
    private BigDecimal originalAmount;
    @MetaData(value = "��Ʒ�Ż��ܽ��", comments = "����Ӱ����Ʒë����ͳ�Ƶ��Żݽ��Զ���̯������Ʒ�Ż��ۿ۽����")
    private BigDecimal discountAmount;
    @MetaData(value = "�����Ż��ܽ��", comments = "������Ӱ����Ʒë���ʵ��Żݽ����ա����۷��á���Ŀ����")
    private BigDecimal otherDiscountAmount;
    @MetaData(value = "Ӧ���ܽ��", comments = "commodityAmount+chargeLogisticsAmount-otherDiscountAmount")
    private BigDecimal totalAmount;
    @MetaData(value = "���տ���", comments = "ʣ�����ա�Ӧ���˿����")
    private BigDecimal payedAmount;

    @MetaData(value = "�տ��ƿ�Ŀ")
    private AccountSubject accountSubject;

    /** ���ռ�¼������ַ��Ϣ  */
    private String deliveryAddr;
    private String mobilePhone;
    private String receivePerson;
    private String postCode;

    @MetaData(value = "ë����")
    private BigDecimal profitRate;

    @MetaData(value = "ë����")
    private BigDecimal profitAmount;

    @MetaData(value = "��ϸ")
    private List<SaleDeliveryDetail> saleDeliveryDetails;

    @MetaData("�ύʱ��")
    private Date submitDate;
    @MetaData("���ʱ��")
    private Date auditDate;
    @MetaData("���ʱ��")
    private Date redwordDate;
    @MetaData(value = "�������ժҪ")
    private String lastOperationSummary;

    @Transient
    @Override
    public String getDisplay() {
        return voucher;
    }

    @Column(length = 128, nullable = false, unique = true, updatable = false)
    @JsonProperty
    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    @Column(length = 256)
    @JsonProperty
    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    @Column(length = 32)
    @JsonProperty
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Column(length = 16)
    @JsonProperty
    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    @Column(length = 16)
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
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

    @Column(length = 1000)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @OneToMany(mappedBy = "saleDelivery", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<SaleDeliveryDetail> getSaleDeliveryDetails() {
        return saleDeliveryDetails;
    }

    public void setSaleDeliveryDetails(List<SaleDeliveryDetail> saleDeliveryDetails) {
        this.saleDeliveryDetails = saleDeliveryDetails;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    @Column(precision = 18, scale = 2, nullable = false)
    public BigDecimal getCommodityAmount() {
        return commodityAmount;
    }

    public void setCommodityAmount(BigDecimal commodityAmount) {
        this.commodityAmount = commodityAmount;
    }

    @Column(precision = 18, scale = 2, nullable = true)
    public BigDecimal getCommodityCostAmount() {
        return commodityCostAmount;
    }

    public void setCommodityCostAmount(BigDecimal commodityCostAmount) {
        this.commodityCostAmount = commodityCostAmount;
    }

    @OneToOne
    @JoinColumn(name = "customer_profile_id", nullable = false)
    @JsonProperty
    public BizTradeUnit getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(BizTradeUnit customerProfile) {
        this.customerProfile = customerProfile;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = true)
    @JsonProperty
    public VoucherStateEnum getVoucherState() {
        return voucherState;
    }

    @SkipParamBind
    public void setVoucherState(VoucherStateEnum voucherState) {
        this.voucherState = voucherState;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getChargeLogisticsAmount() {
        return chargeLogisticsAmount;
    }

    public void setChargeLogisticsAmount(BigDecimal chargeLogisticsAmount) {
        this.chargeLogisticsAmount = chargeLogisticsAmount;
    }

    @Column(precision = 18, scale = 2, nullable = true)
    @JsonProperty
    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
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

    @JsonProperty
    public String getReferenceSource() {
        return referenceSource;
    }

    public void setReferenceSource(String referenceSource) {
        this.referenceSource = referenceSource;
    }

    @JsonProperty
    public String getReferenceVoucher() {
        return referenceVoucher;
    }

    public void setReferenceVoucher(String referenceVoucher) {
        this.referenceVoucher = referenceVoucher;
    }

    @Column(length = 2000)
    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "account_subject_id")
    public AccountSubject getAccountSubject() {
        return accountSubject;
    }

    public void setAccountSubject(AccountSubject accountSubject) {
        this.accountSubject = accountSubject;
    }

    @Formula("(case when total_amount=0 then -1 else (total_amount - commodity_cost_amount)/total_amount end)")
    @JsonProperty
    @NotAudited
    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    @Formula("(total_amount - commodity_cost_amount)")
    @JsonProperty
    @NotAudited
    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getLogisticsAmount() {
        return logisticsAmount;
    }

    @Column(precision = 18, scale = 2, nullable = true)
    public void setLogisticsAmount(BigDecimal logisticsAmount) {
        this.logisticsAmount = logisticsAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    @Column(precision = 18, scale = 2, nullable = true)
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Column(precision = 18, scale = 2, nullable = true)
    public BigDecimal getOtherDiscountAmount() {
        return otherDiscountAmount;
    }

    public void setOtherDiscountAmount(BigDecimal otherDiscountAmount) {
        this.otherDiscountAmount = otherDiscountAmount;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public Date getRedwordDate() {
        return redwordDate;
    }

    public void setRedwordDate(Date redwordDate) {
        this.redwordDate = redwordDate;
    }

    public String getLastOperationSummary() {
        return lastOperationSummary;
    }

    public void setLastOperationSummary(String lastOperationSummary) {
        this.lastOperationSummary = lastOperationSummary;
    }
}
