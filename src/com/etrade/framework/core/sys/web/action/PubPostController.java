package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.PubPost;
import com.etrade.framework.core.sys.service.AttachmentFileService;
import com.etrade.framework.core.sys.service.PubPostService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "�������")
public class PubPostController extends BaseController<PubPost, String> {

    @Autowired
    private PubPostService pubPostService;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Override
    protected BaseService<PubPost, String> getEntityService() {
        return pubPostService;
    }

    @Override
    protected void checkEntityAclPermission(PubPost entity) {
        // Nothing
    }

    @Override
    @MetaData(value = "����")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData(value = "���������б�")
    public HttpHeaders attachmentList() {
        return attachmentList(bindingEntity);
    }

    @MetaData(value = "������������")
    public void attachmentDownload() {
        attachmentDownload(bindingEntity, getRequiredParameter("attachmentId"));
    }
}
