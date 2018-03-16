package com.etrade.framework.core.web;

import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.common.PersistableController;
import com.etrade.framework.core.context.SpringContextHolder;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.entity.AttachmentableEntity;
import com.etrade.framework.core.entity.BaseEntity;
import com.etrade.framework.core.sys.entity.AttachmentFile;
import com.etrade.framework.core.sys.service.AttachmentFileService;
import com.etrade.framework.core.web.util.ServletUtils;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.inject.Inject;

public abstract class BaseController<T extends BaseEntity<ID>, ID extends Serializable> extends
        PersistableController<T, ID> {

    private final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected long imageUploadMaxSize;

    @Inject(value = "struts.image.upload.maxSize", required = false)
    public void setImageUploadMaxSize(String imageUploadMaxSize) {
        this.imageUploadMaxSize = Long.parseLong(imageUploadMaxSize);
    }

    public Long getImageUploadMaxSize() {
        return imageUploadMaxSize;
    }

    protected void prepareClone() {
        //���������Ҫ��ӿ�¡�����ʼ������
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        String clone = this.getParameter("clone");
        if (BooleanUtils.toBoolean(clone)) {
            bindingEntity.setId(null);
            bindingEntity.setVersion(0);
            prepareClone();
        }
    }

    @Override
    @MetaData(value = "����")
    protected HttpHeaders doSave() {
        getEntityService().save(bindingEntity);

        //�������������Զ�������attachmentΪǰ׺�Ĳ���
        if (bindingEntity instanceof AttachmentableEntity) {
            Enumeration<?> names = getRequest().getParameterNames();
            Set<String> attachmentNames = Sets.newHashSet();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (name.startsWith("_attachment_")) {
                    attachmentNames.add(name);
                }
            }
            if (attachmentNames.size() > 0) {
                AttachmentFileService attachmentFileService = SpringContextHolder.getBean(AttachmentFileService.class);
                for (String attachmentName : attachmentNames) {
                    String[] attachments = getParameterIds(attachmentName);
                    attachmentFileService.attachmentBind(attachments, bindingEntity,
                            StringUtils.substringAfter(attachmentName, "_attachment_"));
                }
            }
        }
        setModel(OperationResult.buildSuccessResult("���ݱ���ɹ�", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    protected HttpHeaders attachmentList(T entity) {
        return attachmentList(entity, "default");
    }

    protected HttpHeaders attachmentList(T entity, String category) {
        HttpServletRequest request = getRequest();
        String url = request.getServletPath();
        AttachmentFileService attachmentFileService = SpringContextHolder.getBean(AttachmentFileService.class);
        List<AttachmentFile> attachmentFiles = attachmentFileService.findBy(entity.getClass().getName(),
                String.valueOf(entity.getId()), category);
        List<Map<String, Object>> filesResponse = Lists.newArrayList();
        for (AttachmentFile attachmentFile : attachmentFiles) {
            Map<String, Object> dataMap = Maps.newHashMap();
            dataMap.put("id", attachmentFile.getId());
            dataMap.put("attachmentName", "_attachment_" + attachmentFile.getEntityFileCategory());
            dataMap.put("name", attachmentFile.getFileRealName());
            dataMap.put("size", FileUtils.byteCountToDisplaySize(attachmentFile.getFileLength()));

            dataMap.put("url", getRequest().getContextPath() + StringUtils.substringBefore(url, "!attachmentList")
                    + "!attachmentDownload?id=" + entity.getId() + "&attachmentId=" + attachmentFile.getId());
            filesResponse.add(dataMap);
        }
        Map<String, List<Map<String, Object>>> response = Maps.newHashMap();
        response.put("files", filesResponse);
        setModel(response);
        return buildDefaultHttpHeaders();
    }

    protected void attachmentDownload(T entity, String attachmentId) {
        try {
            AttachmentFileService attachmentFileService = SpringContextHolder.getBean(AttachmentFileService.class);
            AttachmentFile attachmentFile = attachmentFileService.findOne(attachmentId);
            if (attachmentFile != null && entity.getId().toString().equals(attachmentFile.getEntityId())
                    && entity.getClass().getName().equals(attachmentFile.getEntityClassName())) {
                HttpServletResponse response = ServletActionContext.getResponse();
                ServletUtils.setFileDownloadHeader(response, attachmentFile.getFileRealName());
                response.setContentType(attachmentFile.getFileType());

                DynamicConfigService dynamicConfigService = SpringContextHolder.getBean(DynamicConfigService.class);
                String rootPath = dynamicConfigService.getFileUploadRootDir();
                File diskFile = new File(rootPath + attachmentFile.getFileRelativePath() + File.separator
                        + attachmentFile.getDiskFileName());
                logger.debug("Downloading attachment file from disk: {}", diskFile.getAbsolutePath());
                ServletUtils.renderFileDownload(response, FileUtils.readFileToByteArray(diskFile));
            }
        } catch (Exception e) {
            logger.error("Download file error", e);
        }
    }
}
