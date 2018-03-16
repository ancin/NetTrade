package com.etrade.framework.core.dao.jpa;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * ��չJPA Hibernate�־ö���Post�����߼�
 * ��Ҫ��Ϊ�˻�ȡMutablePersistenceUnitInfo����Ӷ����Ի�ȡHibernate EntityԪ����
 * 
 */
public class ExtPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

    private MutablePersistenceUnitInfo mutablePersistenceUnitInfo;

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        this.mutablePersistenceUnitInfo = pui;
    }

    public MutablePersistenceUnitInfo getMutablePersistenceUnitInfo() {
        return mutablePersistenceUnitInfo;
    }
}
