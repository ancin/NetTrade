package com.etrade.biz.stock.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseNativeEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

@MetaData("���䶯��ϸ��¼")
@Entity
@Table(name = "biz_stock_in_out")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class StockInOut extends BaseNativeEntity {

    /**  */
    private static final long serialVersionUID           = 1L;
    @MetaData("����ƾ֤��")
    private String voucher;
    @MetaData(value = "��ƾ֤��")
    private String subVoucher;
    @MetaData("��������")
    private VoucherTypeEnum voucherType;

    @MetaData("��Ʒ�����Ϣ")
    private CommodityStock commodityStock;
    @MetaData("ԭʼ����")
    private BigDecimal originalQuantity = BigDecimal.ZERO;
    @MetaData("ԭʼ������")
    private BigDecimal originalSalingQuantity = BigDecimal.ZERO;
    @MetaData("ԭʼ��;��")
    private BigDecimal originalPurchasingQuantity = BigDecimal.ZERO;

    @MetaData(value = "���������", tooltips = "���ν�����(֮ǰ������-���α����)")
    private BigDecimal quantity = BigDecimal.ZERO;
    @MetaData(value = "�����������", tooltips = "���ν�����(֮ǰ������-���α����)")
    private BigDecimal salingQuantity = BigDecimal.ZERO;
    @MetaData(value = "�������;��", tooltips = "���ν�����(֮ǰ������-���α����)")
    private BigDecimal purchasingQuantity = BigDecimal.ZERO;

    @MetaData("�����仯")
    private BigDecimal diffQuantity = BigDecimal.ZERO;
    @MetaData("�������仯")
    private BigDecimal diffSalingQuantity = BigDecimal.ZERO;
    @MetaData("��;���仯")
    private BigDecimal diffPurchasingQuantity = BigDecimal.ZERO;

    @MetaData("ԭʼ�ɱ���")
    private BigDecimal originalCostPrice = BigDecimal.ZERO;
    @MetaData("ԭʼ����ֵ")
    private BigDecimal originalStockAmount = BigDecimal.ZERO;

    @MetaData("�����ɱ���")
    private BigDecimal costPrice = BigDecimal.ZERO;
    @MetaData("��������ֵ")
    private BigDecimal stockAmount = BigDecimal.ZERO;

    @MetaData(value = "����ժҪ")
    private String operationSummary;
    @MetaData(value = "��ʶ�Ѻ��")
    private Boolean redword;

    public StockInOut() {
        super();
    }

    public StockInOut(String voucher, String subVoucher, VoucherTypeEnum voucherType, CommodityStock commodityStock) {
        BigDecimal zero = BigDecimal.ZERO;
        this.voucher = voucher;
        this.subVoucher = subVoucher;
        this.voucherType = voucherType;
        this.commodityStock = commodityStock;
        this.originalQuantity = commodityStock.getCurStockQuantity();
        this.originalSalingQuantity = commodityStock.getSalingTotalQuantity() == null ? zero : commodityStock
                .getSalingTotalQuantity();
        this.originalPurchasingQuantity = commodityStock.getPurchasingTotalQuantity() == null ? zero : commodityStock
                .getPurchasingTotalQuantity();
        this.originalCostPrice = commodityStock.getCostPrice();
        this.originalStockAmount = commodityStock.getCurStockAmount();
    }

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

    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = true)
    @JsonProperty
    public VoucherTypeEnum getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(VoucherTypeEnum voucherType) {
        this.voucherType = voucherType;
    }

    @ManyToOne
    @JoinColumn(name = "commodity_stock_sid", nullable = false)
    @JsonProperty
    public CommodityStock getCommodityStock() {
        return commodityStock;
    }

    public void setCommodityStock(CommodityStock commodityStock) {
        this.commodityStock = commodityStock;
    }

    @JsonProperty
    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(BigDecimal originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    @JsonProperty
    public BigDecimal getOriginalSalingQuantity() {
        return originalSalingQuantity;
    }

    public void setOriginalSalingQuantity(BigDecimal originalSalingQuantity) {
        this.originalSalingQuantity = originalSalingQuantity;
    }

    @JsonProperty
    public BigDecimal getOriginalPurchasingQuantity() {
        return originalPurchasingQuantity;
    }

    public void setOriginalPurchasingQuantity(BigDecimal originalPurchasingQuantity) {
        this.originalPurchasingQuantity = originalPurchasingQuantity;
    }

    @JsonProperty
    public BigDecimal getOriginalCostPrice() {
        return originalCostPrice;
    }

    public void setOriginalCostPrice(BigDecimal originalCostPrice) {
        this.originalCostPrice = originalCostPrice;
    }

    @JsonProperty
    public BigDecimal getOriginalStockAmount() {
        return originalStockAmount;
    }

    public void setOriginalStockAmount(BigDecimal originalStockAmount) {
        this.originalStockAmount = originalStockAmount;
    }

    @JsonProperty
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @JsonProperty
    public BigDecimal getSalingQuantity() {
        return salingQuantity;
    }

    public void setSalingQuantity(BigDecimal salingQuantity) {
        this.salingQuantity = salingQuantity;
    }

    @JsonProperty
    public BigDecimal getPurchasingQuantity() {
        return purchasingQuantity;
    }

    public void setPurchasingQuantity(BigDecimal purchasingQuantity) {
        this.purchasingQuantity = purchasingQuantity;
    }

    @JsonProperty
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    @JsonProperty
    public BigDecimal getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(BigDecimal stockAmount) {
        this.stockAmount = stockAmount;
    }

    @JsonProperty
    public BigDecimal getDiffQuantity() {
        return diffQuantity;
    }

    public void setDiffQuantity(BigDecimal diffQuantity) {
        this.diffQuantity = diffQuantity;
    }

    @JsonProperty
    public BigDecimal getDiffSalingQuantity() {
        return diffSalingQuantity;
    }

    public void setDiffSalingQuantity(BigDecimal diffSalingQuantity) {
        this.diffSalingQuantity = diffSalingQuantity;
    }

    @JsonProperty
    public BigDecimal getDiffPurchasingQuantity() {
        return diffPurchasingQuantity;
    }

    public void setDiffPurchasingQuantity(BigDecimal diffPurchasingQuantity) {
        this.diffPurchasingQuantity = diffPurchasingQuantity;
    }

    @JsonProperty
    public String getOperationSummary() {
        return operationSummary;
    }

    public void setOperationSummary(String operationSummary) {
        this.operationSummary = operationSummary;
    }

    public void addOperationSummary(String operationSummary) {
        if (StringUtils.isBlank(this.operationSummary)) {
            this.operationSummary = operationSummary;
        } else {
            this.operationSummary += ";" + operationSummary;
        }
    }

    public Boolean getRedword() {
        return redword;
    }

    public void setRedword(Boolean redword) {
        this.redword = redword;
    }
}