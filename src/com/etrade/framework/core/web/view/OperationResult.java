/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.web.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * ����Object��JSON���л��Ķ���ṹ�嶨��
 */
@JsonInclude(Include.NON_NULL)
public class OperationResult {

    /** ��ʶ�����ɹ���� */
    public enum OPERATION_RESULT_TYPE {
        success,
        warning,
        failure,
        confirm
    }

    /** ����success��failure������ʶ */
    private String type;
    /** ���ʻ�����ķ���JSON��Ϣ���� */
    private String message;
    /** ��������� */
    private Object userdata;

    public static OperationResult buildSuccessResult(String message, Object userdata) {
        return new OperationResult(OPERATION_RESULT_TYPE.success, message, userdata);
    }

    public static OperationResult buildSuccessResult(String message) {
        return new OperationResult(OPERATION_RESULT_TYPE.success, message);
    }

    public static OperationResult buildWarningResult(String message, Object userdata) {
        return new OperationResult(OPERATION_RESULT_TYPE.warning, message, userdata);
    }

    public static OperationResult buildFailureResult(String message) {
        return new OperationResult(OPERATION_RESULT_TYPE.failure, message);
    }

    public static OperationResult buildFailureResult(String message, Object userdata) {
        return new OperationResult(OPERATION_RESULT_TYPE.failure, message, userdata);
    }
    
    public static OperationResult buildConfirmResult(String message, Object userdata) {
        return new OperationResult(OPERATION_RESULT_TYPE.confirm, message, userdata);
    }

    public OperationResult(OPERATION_RESULT_TYPE type, String message) {
        this.type = type.name();
        this.message = message;
    }

    public OperationResult(OPERATION_RESULT_TYPE type, String message, Object userdata) {
        this.type = type.name();
        this.message = message;
        this.userdata = userdata;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Object getUserdata() {
        return userdata;
    }

    public void setUserdata(Object userdata) {
        this.userdata = userdata;
    }
}
