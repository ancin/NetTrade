package com.etrade.biz.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.framework.core.annotation.MetaData;

@Service
@MetaData(value = "ƾ֤�����ɷ���", comments = "���ڰ��ռ򻯵�ʵ������ģʽ�����ڸ�����Ҫ�Ż�Ϊ��Ⱥģʽ")
public class VoucherNumGenerateService {

    private final static DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyMMddHHmmss");

    private final static DateFormat DEFAULT_FORMAT_YYMM = new SimpleDateFormat("yyMM");

    public String getVoucherNumByType(VoucherTypeEnum voucherType) {
        if (VoucherTypeEnum.XS.equals(voucherType)) {
            return DEFAULT_FORMAT_YYMM.format(new Date()) + RandomStringUtils.randomNumeric(6);
        }
        String num = DEFAULT_FORMAT.format(new Date()) + RandomStringUtils.randomNumeric(1);
        return voucherType.name() + num;
    }

    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }
}
