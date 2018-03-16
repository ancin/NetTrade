package com.etrade.biz.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etrade.biz.finance.dao.BizTradeUnitDao;
import com.etrade.biz.finance.entity.BizTradeUnit;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.google.common.collect.Lists;

@Service
@Transactional
public class BizTradeUnitService extends BaseService<BizTradeUnit, Long> {

    @Autowired
    private BizTradeUnitDao bizTradeUnitDao;

    @Override
    protected BaseDao<BizTradeUnit, Long> getEntityDao() {
        return bizTradeUnitDao;
    }

    /**
     * ��ѯ�����������ڻ���
     * @return
     */
    public List<BizTradeUnit> findFrequentUsedDatas() {
        //TODO�����Ż�Ϊ���ڳ��òɹ�/���۵�ʵ�ʷ�����ҵ�����ݹ�����ȡ
        return Lists.newArrayList(bizTradeUnitDao.findAll());
    }
}
