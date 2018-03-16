package com.etrade.framework.core.bpm.web.action;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.bpm.service.ActivitiService;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.exception.WebException;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;

public class ActivitiController extends SimpleController {

    protected static Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private FormService formService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    protected ActivitiService activitiService;

    public HttpHeaders showProcessImage() {
        return new DefaultHttpHeaders("process-image").disableCaching();
    }

    /**
     * ��������ͼ��ʾ��Ӧ
     * �˴����õĿ��ɵ�����ͼ���ʿ��ƣ����ҵ����Ҫ��������ͼ�ķ�����Ҫ�����Ӧ�Ŀ����߼�
     */
    @SecurityControlIgnore
    public void processInstanceImage() {
        HttpServletRequest request = ServletActionContext.getRequest();
        InputStream imageStream = null;
        String bizKey = request.getParameter("bizKey");
        if (StringUtils.isNotBlank(bizKey)) {
            imageStream = activitiService.buildProcessImageByBizKey(bizKey);
        } else {
            String processInstanceId = request.getParameter("processInstanceId");
            imageStream = activitiService.buildProcessImageByProcessInstanceId(processInstanceId);
        }

        if (imageStream == null) {
            return;
        }

        // �����Դ���ݵ���Ӧ����
        byte[] b = new byte[1024];
        int len = -1;
        ServletOutputStream out;
        try {
            out = ServletActionContext.getResponse().getOutputStream();
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                out.write(b, 0, len);
            }
            imageStream.close();
            out.close();
        } catch (IOException e) {
            logger.error("Output process image error", e);
            throw new WebException("��������ͼ�����쳣", e);
        }
    }
}
