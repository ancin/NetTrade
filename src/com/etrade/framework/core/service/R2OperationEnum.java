package com.etrade.framework.core.service;

public enum R2OperationEnum {
    
    add("��ӹ���"),

    delete("ɾ������"),

    update("���¹���");

    private String label;

    private R2OperationEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
