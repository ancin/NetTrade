package com.etrade.biz.stock.web.action;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.md.service.CommodityService;
import com.etrade.biz.stock.entity.CommodityStock;
import com.etrade.biz.stock.entity.StockInOut;
import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.biz.stock.service.CommodityStockService;
import com.etrade.biz.stock.service.StockInOutService;
import com.etrade.biz.stock.service.StorageLocationService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.service.Validation;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("��Ʒ���")
public class CommodityStockController extends BaseController<CommodityStock, Long> {

    @Autowired
    private CommodityStockService commodityStockService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private StorageLocationService storageLocationService;
    @Autowired
    private StockInOutService stockInOutService;

    @Override
    protected BaseService<CommodityStock, Long> getEntityService() {
        return commodityStockService;
    }

    @Override
    protected void checkEntityAclPermission(CommodityStock entity) {
        // TODO Auto-generated method stub

    }

    @Override
    @MetaData("����")
    public HttpHeaders doSave() {
        if (bindingEntity.isNotNew()) {
            CommodityStock oldCommodityStock = commodityStockService.findOne(bindingEntity.getId());
            if (!oldCommodityStock.getCurStockQuantity().equals(bindingEntity.getCurStockQuantity())
                    || !oldCommodityStock.getSalingTotalQuantity().equals(bindingEntity.getSalingTotalQuantity())
                    || !oldCommodityStock.getPurchasingTotalQuantity().equals(
                            bindingEntity.getPurchasingTotalQuantity())) {
                StockInOut stockInOut = new StockInOut();
                stockInOut.setCommodityStock(oldCommodityStock);
                stockInOut.setDiffQuantity(bindingEntity.getCurStockQuantity().subtract(
                        oldCommodityStock.getCurStockQuantity()));
                stockInOut.setDiffPurchasingQuantity(bindingEntity.getPurchasingTotalQuantity().subtract(
                        oldCommodityStock.getPurchasingTotalQuantity()));
                stockInOut.setDiffSalingQuantity(bindingEntity.getSalingTotalQuantity().subtract(
                        oldCommodityStock.getSalingTotalQuantity()));
                stockInOut.setOperationSummary("ֱ�ӱ�����������");
                stockInOutService.saveCascade(stockInOut);
            } else {
                bindingEntity.setCurStockAmount(bindingEntity.getCostPrice().multiply(
                        bindingEntity.getCurStockQuantity()));
                getEntityService().save(bindingEntity);
            }
        } else {
            StockInOut stockInOut = new StockInOut();
            stockInOut.setCommodityStock(bindingEntity);
            stockInOut.setDiffQuantity(bindingEntity.getCurStockQuantity());
            stockInOut.setDiffPurchasingQuantity(bindingEntity.getPurchasingTotalQuantity());
            stockInOut.setDiffSalingQuantity(bindingEntity.getSalingTotalQuantity());
            bindingEntity.setCurStockQuantity(BigDecimal.ZERO);
            bindingEntity.setPurchasingTotalQuantity(BigDecimal.ZERO);
            bindingEntity.setSalingTotalQuantity(BigDecimal.ZERO);
            stockInOut.setOperationSummary("ֱ�ӳ�ʼ�����������");
            stockInOutService.saveCascade(stockInOut);
        }
        setModel(OperationResult.buildSuccessResult("���ݱ���ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @Override
    @MetaData("ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData("��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData("�̴���ʾ")
    public HttpHeaders inventory() {
        return buildDefaultHttpHeaders("inventory");
    }

    public HttpHeaders findForInventory() {
        String barcode = getRequiredParameter("barcode");
        Commodity commodity = commodityService.findByBarcode(barcode);
        if (commodity == null) {
            setModel(OperationResult.buildFailureResult("δ֪��Ʒ����: " + barcode));
        } else {
            String batchNo = getParameter("batchNo");
            StorageLocation storageLocation = storageLocationService.findOne(getRequiredParameter("storageLocationId"));
            CommodityStock commodityStock = commodityStockService.findBy(commodity, storageLocation, batchNo);
            Validation.isTrue(commodityStock != null, "�޿�����ݣ�����¼�����ݻ��ȳ�ʼ���������");
            setModel(commodityStock);
        }
        return buildDefaultHttpHeaders();
    }

    @MetaData("�̴�")
    public HttpHeaders doInventory() {
        CommodityStock commodityStock = commodityStockService.findBy(bindingEntity.getCommodity(),
                bindingEntity.getStorageLocation(), bindingEntity.getBatchNo());
        StockInOut stockInOut = new StockInOut();
        stockInOut.setCommodityStock(commodityStock);
        stockInOut.setVoucherType(VoucherTypeEnum.PC);
        stockInOut.setDiffQuantity(bindingEntity.getCurStockQuantity().subtract(commodityStock.getCurStockQuantity()));
        String inventoryExplain = getParameter("inventoryExplain");
        if (StringUtils.isNotBlank(inventoryExplain)) {
            stockInOut.setOperationSummary("�ƶ��̴�:" + inventoryExplain);
        } else {
            stockInOut.setOperationSummary("�ƶ��̴�: �ޱ���Ǽ�");
        }
        stockInOutService.saveCascade(stockInOut);
        setModel(OperationResult.buildSuccessResult("���ݱ���ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "�����ػ��ܿ����")
    public HttpHeaders findByGroupStorageLocation() {
        setModel(findByGroupAggregate("commodity.id", "commodity.sku", "commodity.barcode", "commodity.title",
                "storageLocation.id", "sum(curStockQuantity)", "sum(salingTotalQuantity)",
                "sum(purchasingTotalQuantity)", "sum(stockThresholdQuantity)", "sum(availableQuantity) as sumAvailableQuantity"));
        return buildDefaultHttpHeaders();
    }

    @MetaData(value = "����Ʒ���ܿ����")
    public HttpHeaders findByGroupCommodity() {
        setModel(findByGroupAggregate("commodity.id", "commodity.sku", "commodity.barcode", "commodity.title",
                "sum(curStockQuantity)", "sum(salingTotalQuantity)", "sum(purchasingTotalQuantity)",
                "sum(stockThresholdQuantity)", "sum(availableQuantity)"));
        return buildDefaultHttpHeaders();
    }
}