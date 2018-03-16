package com.etrade.framework.core.sys.web.action;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.ctx.DynamicConfigService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.AttachmentFile;
import com.etrade.framework.core.sys.service.AttachmentFileService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.util.ServletUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@MetaData(value = "��������")
public class AttachmentFileController extends BaseController<AttachmentFile, String> {

    private final Logger logger = LoggerFactory.getLogger(AttachmentFileController.class);

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private DynamicConfigService dynamicConfigService;

    private File[] files;

    private String[] filesFileName;

    private String[] filesContentType;

    @Override
    protected BaseService<AttachmentFile, String> getEntityService() {
        return attachmentFileService;
    }

    @Override
    protected void checkEntityAclPermission(AttachmentFile entity) {
        //Do nothing check
    }

    @Override
    @MetaData(value = "ɾ��")
    public HttpHeaders doDelete() {
        return super.doDelete();
    }

    private AttachmentFile saveAttachmentFile(int idx) throws Exception {
        File file = files[idx];
        AttachmentFile entity = AttachmentFile.buildInstance(file);
        entity.setFileRealName(filesFileName[idx]);
        entity.setFileType(filesContentType[idx]);
        entity.setFileExtension(StringUtils.substringAfterLast(entity.getFileRealName(), "."));

        String rootPath = dynamicConfigService.getFileUploadRootDir();
        File diskFileDir = new File(rootPath + entity.getFileRelativePath());
        if (!diskFileDir.exists()) {
            diskFileDir.mkdirs();
        }
        File diskFile = new File(rootPath + entity.getFileRelativePath() + File.separator + entity.getDiskFileName());
        logger.debug("Saving attachment file to disk: {}", diskFile.getAbsolutePath());
        FileUtils.moveFile(file, diskFile);
        logger.debug("Saving attachment recode to database: {}", entity.getFileRealName());
        attachmentFileService.save(entity);
        return entity;

    }

    @MetaData(value = "���ļ��ϴ�")
    public HttpHeaders uploadMulti() {
        String attachmentName = getRequiredParameter("attachmentName");
        List<Map<String, Object>> filesResponse = Lists.newArrayList();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> dataMap = Maps.newHashMap();
            try {
                AttachmentFile entity = saveAttachmentFile(i);
                dataMap.put("id", entity.getId());
                dataMap.put("attachmentName", attachmentName);
                dataMap.put("name", filesFileName[i]);
                dataMap.put("size", FileUtils.byteCountToDisplaySize(entity.getFileLength()));
                dataMap.put("url", getRequest().getContextPath() + "/sys/attachment-file!download?id=" + entity.getId());
                //                dataMap.put("thumbnailUrl", "thumbnailUrl+" + entity.getId());
                //                dataMap.put("deleteUrl", getRequest().getContextPath() + "/sys/attachment-file!delete.json?id="
                //                        + entity.getId());
                //                dataMap.put("deleteType", "DELETE");
            } catch (Exception e) {
                logger.warn("Attachment file upload failure", e);
                dataMap.put("name", filesFileName[i]);
                dataMap.put("error", e.getMessage());
            }
            filesResponse.add(dataMap);
        }
        Map<String, List<Map<String, Object>>> response = Maps.newHashMap();
        response.put("files", filesResponse);
        setModel(response);
        return buildDefaultHttpHeaders();

    }

    @MetaData(value = "�ļ�����")
    public void download() {
        try {
            AttachmentFile entity = attachmentFileService.findOne(this.getRequiredParameter("id"));
            //�������ʿ��ƣ�δ��������������ͨ�÷������أ��ѹ�������ֻ��ͨ����ҵ�������ڷ���
            Assert.isTrue(StringUtils.isBlank(entity.getEntityId()));
            HttpServletResponse response = ServletActionContext.getResponse();
            ServletUtils.setFileDownloadHeader(response, entity.getFileRealName());
            response.setContentType(entity.getFileType());
            String rootPath = dynamicConfigService.getFileUploadRootDir();
            File diskFile = new File(rootPath + entity.getFileRelativePath() + File.separator
                    + entity.getDiskFileName());

            ServletUtils.renderFileDownload(response, FileUtils.readFileToByteArray(diskFile));
        } catch (Exception e) {
            logger.error("Download file error", e);
        }
    }

    @MetaData(value = "�ļ�ɾ��")
    public HttpHeaders delete() {
        AttachmentFile entity = attachmentFileService.findOne(this.getRequiredParameter("id"));
        List<Map<String, Object>> filesResponse = Lists.newArrayList();
        if (entity != null && entity.getEntityId() == null) {
            Map<String, Object> dataMap = Maps.newHashMap();
            dataMap.put(entity.getFileRealName(), true);
            attachmentFileService.delete(entity);
            filesResponse.add(dataMap);
        }
        Map<String, List<Map<String, Object>>> response = Maps.newHashMap();
        response.put("files", filesResponse);
        setModel(response);
        return buildDefaultHttpHeaders();
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public String[] getFilesFileName() {
        return filesFileName;
    }

    public void setFilesFileName(String[] filesFileName) {
        this.filesFileName = filesFileName;
    }

    public String[] getFilesContentType() {
        return filesContentType;
    }

    public void setFilesContentType(String[] filesContentType) {
        this.filesContentType = filesContentType;
    }
}
