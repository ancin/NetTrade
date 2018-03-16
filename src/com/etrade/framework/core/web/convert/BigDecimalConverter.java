package com.etrade.framework.core.web.convert;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.StrutsTypeConverter;

/**
 * ����BigDecimalĬ�ϻ�ת��Ϊ��ѧ��������λ���ϳ�
 * ���ƻ�ת��ʵ��ָ����ʾС��λ��
 */
public class BigDecimalConverter extends StrutsTypeConverter {
    @SuppressWarnings("rawtypes")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (BigDecimal.class.equals(toClass)) {
            String str = values[0];
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            BigDecimal d = new BigDecimal(str);
            return d;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof BigDecimal) {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(o);
        }
        return "";
    }
}
