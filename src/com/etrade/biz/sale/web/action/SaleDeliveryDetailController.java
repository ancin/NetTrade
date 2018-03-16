package com.etrade.biz.sale.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.sale.entity.SaleDeliveryDetail;
import com.etrade.biz.sale.service.SaleDeliveryDetailService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;

@MetaData("����(����)����ϸ����")
public class SaleDeliveryDetailController extends BaseController<SaleDeliveryDetail, Long> {

    @Autowired
    private SaleDeliveryDetailService saleDeliveryDetailService;

    @Override
    protected BaseService<SaleDeliveryDetail, Long> getEntityService() {
        return saleDeliveryDetailService;
    }

    @Override
    protected void checkEntityAclPermission(SaleDeliveryDetail entity) {
        // TODO Add acl check code logic
    }

    @Override
    @MetaData("��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData(value = "������Ʒë��ͳ��", comments = "���ڿ��ܳ�����ȫ��Ʒ���͵�0���۶������Ҫ����case when�жϴ���������ֳ������")
    public HttpHeaders findByGroupCommodity() {
        setModel(findByGroupAggregate("commodity.id", "commodity.sku", "commodity.title",
                "max(case(equal(amount,0),-1,quot(diff(amount,costAmount),amount))) as maxProfitRate",
                "min(case(equal(amount,0),-1,quot(diff(amount,costAmount),amount))) as minProfitRate",
                "sum(diff(amount,costAmount)) as sumProfitAmount", "sum(amount)", "sum(quantity)",
                "case(equal(sum(amount),0),-1,quot(sum(diff(amount,costAmount)),sum(amount))) as avgProfitRate"));
        return buildDefaultHttpHeaders();
    }
}