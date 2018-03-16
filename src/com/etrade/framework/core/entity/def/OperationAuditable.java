package com.etrade.framework.core.entity.def;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public interface OperationAuditable {

    /**
     * ת������״̬����ֵΪ������ʾ�ַ���
     * @return
     */
    public abstract String convertStateToDisplay(String rawState);

}
