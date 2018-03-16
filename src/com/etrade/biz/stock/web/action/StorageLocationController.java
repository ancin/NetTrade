package com.etrade.biz.stock.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.biz.stock.entity.StorageLocation;
import com.etrade.biz.stock.service.StorageLocationService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("���ع���")
public class StorageLocationController extends BaseController<StorageLocation,String> {

    @Autowired
    private StorageLocationService storageLocationService;

    @Override
    protected BaseService<StorageLocation, String> getEntityService() {
        return storageLocationService;
    }
    
    @Override
    protected void checkEntityAclPermission(StorageLocation entity) {
        // TODO Add acl check code logic
    }

    @MetaData("[TODO��������]")
    public HttpHeaders todo() {
        //TODO
        setModel(OperationResult.buildSuccessResult("TODO�������"));
        return buildDefaultHttpHeaders();
    }
    
    @Override
    @MetaData("����")
    public HttpHeaders doCreate() {
        return super.doCreate();
    }

    @Override
    @MetaData("����")
    public HttpHeaders doUpdate() {
        return super.doUpdate();
    }
    
    @Override
    @MetaData("����")
    public HttpHeaders doSave() {
        return super.doSave();
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
    
    @Override
    @MetaData(value = "������ѡ������")
    public HttpHeaders selectOptions() {
        return super.selectOptions();
    }
}