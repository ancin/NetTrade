package com.etrade.biz.stock.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.etrade.biz.md.entity.Commodity;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseNativeEntity;
import com.etrade.framework.core.web.json.DateJsonSerializer;
import com.etrade.framework.core.web.json.EntityIdDisplaySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@MetaData("��Ʒ�������")
@Entity
@Table(name = "biz_stock_commodity", uniqueConstraints = @UniqueConstraint(columnNames = { "Storage_Location_SID",
        "COMMODITY_SID", "batchNo" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class CommodityStock extends BaseNativeEntity {
    /**  */
    private static final long serialVersionUID        = 1L;

    @MetaData(value = "�����Ʒ")
    private Commodity commodity;

    @MetaData(value = "����")
    private StorageLocation storageLocation;

    @MetaData(value = "ʵ�������� ", comments = "�ɹ�������ʱ��Ȩ���¼���")
    private BigDecimal curStockQuantity = BigDecimal.ZERO;

    @MetaData(value = "������������", comments = "�ɹ����ύʱ�ۼӴ����ԣ��������ʱ�ۼ�������ֵ")
    private BigDecimal salingTotalQuantity = BigDecimal.ZERO;

    @MetaData(value = "�ɹ���;����", comments = "�ɹ��������ͨ��ʱ�ۼӴ����ԣ��ɹ������ջ����ʱ�ۼ�������ֵ")
    private BigDecimal purchasingTotalQuantity = BigDecimal.ZERO;

    @MetaData(value = "���Ԥ��ֵ")
    private BigDecimal stockThresholdQuantity = BigDecimal.ZERO;

    @MetaData(value = "�ڳ�����", comments = "��ĩ��ת����")
    private BigDecimal quantity00 = BigDecimal.ZERO;
    @MetaData(value = "�ڳ��ɱ����", comments = "��ĩ��ת����")
    private BigDecimal costAmount00 = BigDecimal.ZERO;
    @MetaData(value = "�ڳ��ɱ���", comments = "��ĩ��ת���£�costAmount00/quantity00")
    private BigDecimal costPrice00 = BigDecimal.ZERO;

    @MetaData(value = "��ǰ�ɱ���", comments = "�ɹ�������ʱ��Ȩ���¼���ɱ���=(curStockAmount+�����)/(curStockQuantity+�������)")
    private BigDecimal costPrice = BigDecimal.ZERO;

    @MetaData(value = "��ǰ�ɱ����", comments = "�ɹ�������ʱ��Ȩ���¼���")
    private BigDecimal curStockAmount = BigDecimal.ZERO;
    @MetaData("���κ�")
    private String batchNo;
    @MetaData("��������")
    private Date productDate;
    @MetaData("��������")
    private Date expireDate;

    private BigDecimal availableQuantity;

    @ManyToOne
    @JoinColumn(name = "Commodity_SID", nullable = false, updatable = false)
    @JsonProperty
    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    @OneToOne
    @JoinColumn(name = "Storage_Location_SID", nullable = false, updatable = false)
    @JsonProperty
    @JsonSerialize(using = EntityIdDisplaySerializer.class)
    @NotAudited
    public StorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getCurStockQuantity() {
        return curStockQuantity;
    }

    public void setCurStockQuantity(BigDecimal curStockQuantity) {
        this.curStockQuantity = curStockQuantity;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getQuantity00() {
        return quantity00;
    }

    public void setQuantity00(BigDecimal quantity00) {
        this.quantity00 = quantity00;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCostAmount00() {
        return costAmount00;
    }

    public void setCostAmount00(BigDecimal costAmount00) {
        this.costAmount00 = costAmount00;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getCurStockAmount() {
        return curStockAmount;
    }

    public void setCurStockAmount(BigDecimal curStockAmount) {
        this.curStockAmount = curStockAmount;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getStockThresholdQuantity() {
        return stockThresholdQuantity;
    }

    public void setStockThresholdQuantity(BigDecimal stockThresholdQuantity) {
        this.stockThresholdQuantity = stockThresholdQuantity;
    }

    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    public BigDecimal getPurchasingTotalQuantity() {
        return purchasingTotalQuantity;
    }

    public void setPurchasingTotalQuantity(BigDecimal purchasingTotalQuantity) {
        this.purchasingTotalQuantity = purchasingTotalQuantity;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getCostPrice00() {
        return costPrice00;
    }

    public void setCostPrice00(BigDecimal costPrice00) {
        this.costPrice00 = costPrice00;
    }

    @Column(precision = 18, scale = 2)
    @JsonProperty
    public BigDecimal getSalingTotalQuantity() {
        return salingTotalQuantity;
    }

    public void setSalingTotalQuantity(BigDecimal salingTotalQuantity) {
        this.salingTotalQuantity = salingTotalQuantity;
    }

    @Formula("(cur_Stock_Quantity + purchasing_Total_Quantity - saling_Total_Quantity - stock_Threshold_Quantity)")
    @JsonProperty
    @NotAudited
    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Column(length = 128, updatable = false)
    @JsonProperty
    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
