package com.etrade.biz.purchase.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.annotation.MetaData;
import com.fasterxml.jackson.annotation.JsonProperty;

@MetaData("�ɹ���������")
@Entity
@Table(name = "myt_purchase_order_detail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class PurchaseOrderDetail extends BaseBizEntity {
    /**  */
    private static final long serialVersionUID = 1L;
    @MetaData(value = "��ƾ֤��")
    private String subVoucher;
    @MetaData("�ɹ�����")
    private PurchaseOrder purchaseOrder;
    @MetaData("��Ʒ")
    private Commodity commodity;
    @MetaData("����")
    private StorageLocation storageLocation;

    @MetaData("�ɹ���")
    private BigDecimal quantity;

    @MetaData("�ɹ��۸�")
    private BigDecimal price;

    @MetaData("�������ջ���")
    private BigDecimal recvQuantity = BigDecimal.ZERO;
    @MetaData("��λ")
    private String measureUnit;

    @MetaData("ԭ�۽��")
    private BigDecimal originalAmount;

    @MetaData("�ۿ���(%)")
    private BigDecimal discountRate;

    @MetaData("�ۿ۶�")
    private BigDecimal discountAmount;

    @MetaData("�ۺ���")
    private BigDecimal amount;
    /**˰��(%) */
    /*private BigDecimal taxRate;
    *//** ˰�� */
    /*
    private BigDecimal taxAmount;
    *//** ��˰�ܽ�� */
    /*
    private BigDecimal commodityAndTaxAmount;*/

    @MetaData("��̯�˷� ")
    private BigDecimal deliveryAmount;

    @MetaData("���ɱ���")
    private BigDecimal costPrice;

    @MetaData("�ɹ���Ʒ�ɱ����")
    private BigDecimal costAmount;

    @Column(length = 128, nullable = true)
    @JsonProperty
    public String getSubVoucher() {
        return subVoucher;
    }

    public void setSubVoucher(String subVoucher) {
        this.subVoucher = subVoucher;
    }

    @ManyToOne
    @JoinColumn(name = "ORDER_SID", nullable = false)
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "COMMODITY_SID", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonProperty
    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getRecvQuantity() {
        return recvQuantity;
    }

    public void setRecvQuantity(BigDecimal recvQuantity) {
        this.recvQuantity = recvQuantity;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "storage_location_sid")
    @NotAudited
    @JsonProperty
    public StorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    @JsonProperty
    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /*@Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCommodityAndTaxAmount() {
        return commodityAndTaxAmount;
    }

    public void setCommodityAndTaxAmount(BigDecimal commodityAndTaxAmount) {
        this.commodityAndTaxAmount = commodityAndTaxAmount;
    }
    */
    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

}