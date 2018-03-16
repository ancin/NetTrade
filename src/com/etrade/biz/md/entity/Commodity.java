package com.etrade.biz.md.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "biz_commodity")
@MetaData(value = "��Ʒ")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class Commodity extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    @MetaData(value = "Ψһ����")
    private String sku;

    @MetaData(value = "ʵ������")
    private String barcode;

    @MetaData(value = "��Ʒ����")
    private String title;

    @MetaData(value = "�ɱ���")
    private BigDecimal costPrice;

    @MetaData(value = "���ۼ�")
    private BigDecimal salePrice;

    @MetaData(value = "Ĭ�Ͽ���", tooltips = "���ڲɹ�������ʱ��ʼ����")
    private StorageLocation defaultStorageLocation;

    @MetaData(value = "������", tooltips = "��ʾ��Ʒ��Ϣ�������ڲ��ɹ���״̬")
    private Boolean soldOut = Boolean.FALSE;

    @MetaData(value = "���¼�", tooltips = "����ʾ��Ʒ��Ϣֻ��ʾ��Ʒ���¼�")
    private Boolean removed = Boolean.FALSE;

    @Override
    @Transient
    @JsonProperty
    public String getDisplay() {
        return sku + " " + title;
    }

    @Column(length = 64, unique = true, nullable = false)
    @JsonProperty
    @Size(min = 5, max = 10)
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @JsonProperty
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonProperty
    @Column(length = 256, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    @JsonProperty
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "Default_Storage_Location_ID")
    @NotAudited
    @JsonProperty
    public StorageLocation getDefaultStorageLocation() {
        return defaultStorageLocation;
    }

    public void setDefaultStorageLocation(StorageLocation defaultStorageLocation) {
        this.defaultStorageLocation = defaultStorageLocation;
    }

    @Column(nullable = false)
    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    @Column(nullable = false)
    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

}
