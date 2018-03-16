package com.etrade.biz.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.framework.core.annotation.MetaData;
import com.fasterxml.jackson.annotation.JsonProperty;

@MetaData("ҵ��������λ")
@Entity
@Table(name = "biz_trade_unit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BizTradeUnit extends BaseBizEntity {

    /**  */
    private static final long    serialVersionUID = 1L;

    @MetaData(value = "����")
    private String code;

    @MetaData(value = "����")
    private String name;

    @MetaData(value = "��λ����")
    private BizTradeUnitTypeEnum type;

    @MetaData(value = "��ַ")
    private String addr;

    @MetaData(value = "�칫�绰")
    private String officePhone;

    @MetaData(value = "�ƶ��绰")
    private String mobilePhone;

    public static enum BizTradeUnitTypeEnum {

        @MetaData(value = "�ͻ�")
        CUSTOMER,

        @MetaData(value = "��Ӧ��")
        SUPPLIER;

    }

    @Override
    @Transient
    @JsonProperty
    public String getDisplay() {
        return code + " " + name;
    }

    @Column(nullable = false, length = 25, unique = true)
    @JsonProperty
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 128, nullable = false)
    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    @JsonProperty
    public BizTradeUnitTypeEnum getType() {
        return type;
    }

    public void setType(BizTradeUnitTypeEnum type) {
        this.type = type;
    }

    @Column(length = 1000)
    @JsonProperty
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Column(length = 128)
    @JsonProperty
    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    @Column(length = 128)
    @JsonProperty
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
