package com.etrade.framework.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ��ʶ����֧�ֹ������������ɿ���Զ���������ذ󶨺�������
 * ��Ҫ֧�ָ���������ʵ�����ʵ�ָýӿڣ�����һ�����Դ�Ź�����������
 */
public interface AttachmentableEntity {

    /**
     * ������������
     * @return
     */
    @JsonProperty
    public Integer getAttachmentSize();
}
