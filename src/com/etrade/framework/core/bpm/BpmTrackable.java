package com.etrade.framework.core.bpm;

import java.io.Serializable;

import javax.persistence.Transient;

import com.etrade.framework.core.entity.annotation.SkipParamBind;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ��ʶʵ������BPM���̴�����̻ص���д�뵱ǰҵ����������Ĺ������ڵ�
 * һ����ʵ�嶨��private String activeTaskName���ԣ�Ȼ�����ɶ�Ӧ��setter��getter��ʽ����
 */
public interface BpmTrackable {

    @Transient
    @JsonIgnore
    public String getBpmBusinessKey();

    @JsonProperty
    public String getActiveTaskName();

    @SkipParamBind
    public void setActiveTaskName(String activeTaskName);

    @JsonProperty
    Serializable getId();
}
