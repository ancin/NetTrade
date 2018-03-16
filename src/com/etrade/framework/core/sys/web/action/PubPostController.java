package com.etrade.framework.core.sys.web.action;

import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.PubPost;
import com.etrade.framework.core.sys.service.AttachmentFileService;
import com.etrade.framework.core.sys.service.PubPostService;
import com.etrade.framework.core.web.BaseController;

@MetaData(value = "公告管理")
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
    @MetaData(value = "保存")
    public HttpHeaders doSave() {
        return super.doSave();
    }

    @Override
    @MetaData(value = "删除")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    @Override
    @MetaData(value = "查询")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    @MetaData(value = "关联附件列表")
    public HttpHeaders attachmentList() {
        return attachmentList(bindingEntity);
    }

    @MetaData(value = "关联附件下载")
    public void attachmentDownload() {
        attachmentDownload(bindingEntity, getRequiredParameter("attachmentId"));
    }
}
