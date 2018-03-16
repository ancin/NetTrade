package com.etrade.framework.core.web.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.struts2.rest.DefaultContentTypeHandlerManager;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * ��ӻ���Request Header��Accept��Ϣ�������ʹ�����
 */
public class NegotiationContentTypeHandlerManager extends DefaultContentTypeHandlerManager {

    private final Logger logger = LoggerFactory.getLogger(NegotiationContentTypeHandlerManager.class);

    /** ContentTypeHandlers keyed by the extension */
    Map<String, ContentTypeHandler> handlersByExtensionExt;

    @SuppressWarnings("unchecked")
    @Override
    @Inject
    public void setContainer(Container container) {
        if (handlersByExtensionExt == null) {
            super.setContainer(container);
            try {
                handlersByExtensionExt = (Map<String, ContentTypeHandler>) FieldUtils.readField(this,
                        "handlersByExtension", true);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public ContentTypeHandler getHandlerForRequest(HttpServletRequest request) {
        ContentTypeHandler httpHeaderContentTypeHandler = getHandlerByHttpHeader(request);
        return httpHeaderContentTypeHandler != null ? httpHeaderContentTypeHandler : super
                .getHandlerForRequest(request);
    }

    @Override
    public ContentTypeHandler getHandlerForResponse(HttpServletRequest request, HttpServletResponse response) {
        ContentTypeHandler httpHeaderContentTypeHandler = getHandlerByHttpHeader(request);
        return httpHeaderContentTypeHandler != null ? httpHeaderContentTypeHandler : super.getHandlerForResponse(
                request, response);
    }

    private ContentTypeHandler getHandlerByHttpHeader(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (StringUtils.isNotBlank(acceptHeader)) {
            if (acceptHeader.indexOf("json") > -1) {
                return handlersByExtensionExt.get("json");
            }
        }
        return null;
    }
}
