package com.etrade.framework.core.security;

import java.util.Collection;
import java.util.Map;

public interface AclService {
    /**
     * ��ACL Codeת��Ϊ��Ӧ��ACL Type
     * 
     * @param aclCode
     * @return
     */
    String aclCodeToType(String aclCode);

    /**
     * ��ȡ����ACL Type���Ͷ���Map
     * 
     * @return
     */
    Map<String, String> getAclTypeMap();

    /**
     * ��ȡ����ACL Code Map�ṹ����
     * 
     * @return
     */
    Map<String, String> findAclCodesMap();

    /**
     * ��ȡACL Codeǰ׺
     * 
     * @return
     */
    String getAclCodePrefix(String aclCode);

    /**
     * ��ȡ��¼�û�ACL Codeǰ׺
     * 
     * @return
     */
    String getLogonUserAclCodePrefix();

    /**
     * ����һ����һACL Code��������Է��ʵ�ACL Codeǰ׺����
     * ���û�ACL CodeΪ120000������ҵ����������ǰ׺���Ͽ�ת��12, AA12,BB12��
     * @return
     */
    Collection<String> getStatAclCodePrefixs(String aclCode);

    /**
     * �����Է��չ������������뷽ʽ����ACL���ƴ��룬��112100����ȡ������0��׺֮ǰ��������
     * ����������ACL������бȶ����ж��û������ݵķ���Ȩ��
     * 
     * @exception ����ж���Ȩ����,���׳������쳣
     */
    void validateAuthUserAclCodePermission(String... dataAclCode);
}
