package com.etrade.biz.purchase.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.finance.dao.AccountSubjectDao;
import com.etrade.biz.finance.dao.BizTradeUnitDao;
import com.etrade.biz.finance.entity.AccountInOut;
import com.etrade.biz.finance.service.AccountInOutService;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.purchase.dao.PurchaseOrderDao;
import com.etrade.biz.purchase.entity.PurchaseOrder;
import com.etrade.biz.purchase.entity.PurchaseOrderDetail;
import com.etrade.biz.stock.dao.StockInOutDao;
import com.etrade.biz.stock.entity.CommodityStock;
import com.etrade.biz.stock.entity.StockInOut;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.biz.stock.service.CommodityStockService;
import com.etrade.biz.stock.service.StockInOutService;
import com.etrade.framework.core.bpm.service.ActivitiService;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.google.common.collect.Lists;

@Service
@Transactional
public class PurchaseOrderService extends BaseService<PurchaseOrder, Long> {

    @Autowired
    private AccountInOutService accountInOutService;

    @Autowired
    private PurchaseOrderDao purchaseOrderDao;

    @Autowired
    private BizTradeUnitDao bizTradeUnitDao;

    @Autowired
    private AccountSubjectDao accountSubjectDao;

    @Autowired
    private CommodityStockService commodityStockService;

    @Autowired
    private StockInOutDao stockInOutDao;

    @Autowired(required = false)
    private ActivitiService activitiService;

    @Autowired
    private StockInOutService stockInOutService;

    @Override
    protected BaseDao<PurchaseOrder, Long> getEntityDao() {
        return purchaseOrderDao;
    }

    public void bpmCreate(PurchaseOrder entity, Map<String, Object> variables) {
        this.save(entity);
        activitiService.startProcessInstanceByKey("BPM_PURCHASE_ORDER", entity);
    }

    public void bpmUpdate(PurchaseOrder entity, String taskId, Map<String, Object> variables) {
        this.save(entity);
        activitiService.completeTask(taskId, variables);
    }

    private AccountInOut buildDefaultAccountInOut(PurchaseOrder entity) {
        AccountInOut accountInOut = new AccountInOut();
        accountInOut.setVoucher(entity.getVoucher());
        accountInOut.setVoucherType(VoucherTypeEnum.JHD);
        accountInOut.setPostingDate(entity.getVoucherDate());
        return accountInOut;
    }

    public void bpmPay(PurchaseOrder entity, String taskId) {
        List<AccountInOut> accountInOuts = Lists.newArrayList();

        //借：1123=预付账款
        AccountInOut accountInOut1 = buildDefaultAccountInOut(entity);
        accountInOut1.setAccountSubject(accountSubjectDao.findByCode("1123"));
        accountInOut1.setAccountSummary("采购订单[" + entity.getVoucher() + "]预付款");
        accountInOut1.setAmount(entity.getActualPayedAmount());
        accountInOut1.setAccountDirection(true);
        accountInOut1.setBizTradeUnit(bizTradeUnitDao.findOne(entity.getBizTradeUnit().getId()));
        accountInOuts.add(accountInOut1);

        //贷：用户选择结算科目，如1001=库存现金
        AccountInOut accountInOut2 = buildDefaultAccountInOut(entity);
        accountInOut2.setAccountSubject(entity.getAccountSubject());
        accountInOut2.setAccountSummary("采购订单[" + entity.getVoucher() + "]预付款");
        accountInOut2.setAmount(entity.getActualPayedAmount());
        accountInOut2.setAccountDirection(false);
        accountInOuts.add(accountInOut2);

        accountInOutService.saveBalance(accountInOuts);

        bpmUpdate(entity, taskId, null);
    }

    public void bpmDelivery(PurchaseOrder entity, String taskId) {
        List<PurchaseOrderDetail> purchaseOrderDetails = entity.getPurchaseOrderDetails();
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            Commodity commodity = purchaseOrderDetail.getCommodity();
            StorageLocation storageLocation = purchaseOrderDetail.getStorageLocation();
            CommodityStock commodityStock = commodityStockService.findBy(commodity, storageLocation);
            if (commodityStock == null) {
                commodityStock = new CommodityStock();
                commodityStock.setCommodity(commodity);
                commodityStock.setStorageLocation(storageLocation);
                commodityStock.setCostPrice(purchaseOrderDetail.getPrice());
            }

            StockInOut stockInOut = new StockInOut(entity.getVoucher(), purchaseOrderDetail.getSubVoucher(),
                    VoucherTypeEnum.JHD, commodityStock);
            stockInOut.setDiffPurchasingQuantity(purchaseOrderDetail.getQuantity());
            stockInOut.setOperationSummary("采购订单更新在途库存量");
            stockInOutService.saveCascade(stockInOut);
        }
        bpmUpdate(entity, taskId, null);
    }

    /**
     * 单据红冲
     * @param entity
     */
    public void redword(PurchaseOrder entity) {
        Assert.isTrue(entity.getRedwordDate() == null);

        //移除工作流实例
        activitiService.deleteProcessInstanceByEntity(entity);

        entity.setRedwordDate(new Date());
        entity.setLastOperationSummary(entity.buildLastOperationSummary("红冲"));
        purchaseOrderDao.save(entity);

        //克隆创建红冲对象，只复制主对象，明细行项数据不复制
        PurchaseOrder redwordTarget = new PurchaseOrder();
        String[] copyIgnoreProperties = ArrayUtils.addAll(entity.retriveCommonProperties(), new String[] {
                "purchaseOrderDetails", "voucher", "voucherDate" });
        BeanUtils.copyProperties(entity, redwordTarget, copyIgnoreProperties);
        redwordTarget.setVoucherDate(new Date());
        redwordTarget.setVoucher("R" + entity.getVoucher());
        purchaseOrderDao.save(redwordTarget);

        //处理库存红冲退回
        stockInOutService.redword(entity.getVoucher(), VoucherTypeEnum.JHD, redwordTarget.getVoucher());

        //处理财务红冲退回
        accountInOutService.redword(entity.getVoucher(), VoucherTypeEnum.JHD, redwordTarget.getVoucher());
    }

    @Override
    public void delete(PurchaseOrder entity) {
        activitiService.deleteProcessInstanceByEntity(entity);
        super.delete(entity);
    }
}
