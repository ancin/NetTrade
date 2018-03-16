package com.etrade.framework.core.entity.def;

import com.etrade.framework.core.annotation.MetaData;

public enum DynamicParameterTypeEnum {

    @MetaData(value = "����", comments = "����ʱ�ֵ�����������,��ʽyyyy-MM-dd")
    DATE,

    @MetaData(value = "����ʱ��", comments = "��ʱ�ֵ�����,��ʽ��yyyy-MM-dd HH:mm:ss")
    TIMESTAMP,

    @MetaData("������")
    FLOAT,

    @MetaData("����")
    INTEGER,

    @MetaData("�Ƿ񲼶���")
    BOOLEAN,

    @MetaData(value = "ö�����ݶ���", comments = "����ö�ٶ����name()��getLabel()���ض�Ӧ��key1-value1�ṹ����,��Ӧ��listDataSourceд��ʾ����demo.po.entity.PurchaseOrder$PurchaseOrderTypEnum")
    ENUM,

    @MetaData(value = "�����ֵ������б�", comments = "�ṩ�����ֵ����CATEGORY��Ӧ��key1-value1�ṹ����,��Ӧ��listDataSourceд��ʾ����PRIVILEGE_CATEGORY")
    DATA_DICT_LIST,

    @MetaData(value = "SQL��ѯ�����б�", comments = "ֱ����SQL�����ʽ����key-value�ṹ����,��Ӧ��listDataSourceд��ʾ����select role_code,role_name from t_sys_role")
    SQL_LIST,

    @MetaData(value = "OGNL�﷨��ֵ��", comments = "��OGNL�﷨����key-value�ṹ����,��Ӧ��listDataSourceд��ʾ����#{'A':'ClassA','B':'ClassB'}")
    OGNL_LIST,

    @MetaData("�����ı�")
    MULTI_TEXT,

    @MetaData("HTML�ı�")
    HTML_TEXT,

    @MetaData("�����ı�")
    STRING;

}
