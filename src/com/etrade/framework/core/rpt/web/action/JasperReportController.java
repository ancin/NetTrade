package com.etrade.framework.core.rpt.web.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.apache.struts2.views.jasperreports.JasperReportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.exception.WebException;
import com.etrade.framework.core.rpt.entity.ReportDef;
import com.etrade.framework.core.rpt.service.ReportDefService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.AttachmentFile;
import com.etrade.framework.core.sys.service.DataDictService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.google.common.collect.Maps;

public class JasperReportController extends BaseController<ReportDef, String> {

    private final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

    private static String WEB_ROOT_DIR = null;

    private static final String PROPERTY_WORKBOOK_TEMPLATE_FILENAME = "net.sf.jasperreports.export.xls.workbook.template.filename";

    private static final String JASPER_TEMPLATE_FILE_DIR = File.separator + "template" + File.separator + "jasper";

    @Autowired
    private ReportDefService reportDefService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataDictService dataDictService;

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new WebException(e.getMessage(), e);
        }
    }

    @Override
    protected BaseService<ReportDef, String> getEntityService() {
        return reportDefService;
    }

    @Override
    protected void checkEntityAclPermission(ReportDef entity) {
        // Do nothing
    }

    @SecurityControlIgnore
    public String preview() {
        return "preview";
    }

    @SecurityControlIgnore
    public String generate() {
        return "jasperResult";
    }

    private String getWebRootDir() {
        if (WEB_ROOT_DIR == null) {
            WEB_ROOT_DIR = ServletActionContext.getServletContext().getRealPath("/");
        }
        return WEB_ROOT_DIR;
    }

    private String getRelativeJasperFilePath() {
        return File.separator + "WEB-INF" + File.separator + JASPER_TEMPLATE_FILE_DIR;
    }

    private File getTargetJasperFile(String reportId) {
        return new File(getWebRootDir() + getRelativeJasperFilePath() + File.separator + reportId + ".jasper");
    }

    @SecurityControlIgnore
    public String getLocation() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String reportId = request.getParameter("report");
        File targetJasperFile = getTargetJasperFile(reportId);
        File targetJrxmlFile = new File(getWebRootDir() + getRelativeJasperFilePath() + File.separator + reportId
                + ".jrxml");
        try {
            if (!targetJrxmlFile.exists()) {
                targetJrxmlFile.createNewFile();
            }
            logger.debug("Using jrxml file: {}", targetJrxmlFile.getAbsolutePath());
            logger.debug("Using jasper file: {}", targetJasperFile.getAbsolutePath());
            ReportDef reportDef = reportDefService.findByCode(reportId);
            AttachmentFile attachmentFile = null;
            if (reportDef != null) {
                attachmentFile = reportDef.getTemplateFile();
            }
            boolean needUpdateJasperFile = false;
            if (!targetJasperFile.exists()) {
                needUpdateJasperFile = true;
                if (!targetJasperFile.exists()) {
                    targetJasperFile.createNewFile();
                }
            } else {

                if (attachmentFile != null) {
                    //TODO
                    //                    //���ݶ����жϴ���
                    //                    long compareTime = attachmentFile.getLastModifiedDate() != null ? attachmentFile
                    //                            .getLastModifiedDate().getTime() : attachmentFile.getCreatedDate().getTime();
                    //                    if (targetJasperFile.lastModified() < compareTime) {
                    //                        needUpdateJasperFile = true;
                    //                        FileCopyUtils.copy(attachmentFile.getFileContent(), targetJrxmlFile);
                    //                    }
                } else {
                    if (targetJrxmlFile.lastModified() > targetJasperFile.lastModified()) {
                        needUpdateJasperFile = true;
                    }
                }
            }
            if (needUpdateJasperFile) {
                logger.info("Compiling jasper file: {}", targetJasperFile.getAbsolutePath());
                JasperCompileManager.compileReportToFile(targetJrxmlFile.getAbsolutePath(),
                        targetJasperFile.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WebException(e.getMessage(), e);
        }
        return getRelativeJasperFilePath() + File.separator + reportId + ".jasper";
    }

    private static Map<String, String> jasperOutputFormatMap;

    public Map<String, String> getJasperOutputFormatMap() {
        if (jasperOutputFormatMap == null) {
            jasperOutputFormatMap = new LinkedHashMap<String, String>();
            jasperOutputFormatMap.put(JasperReportConstants.FORMAT_PDF, "Adobe PDF");
            jasperOutputFormatMap.put(JasperReportConstants.FORMAT_XLS, "Excel");
            jasperOutputFormatMap.put(JasperReportConstants.FORMAT_HTML, "HTML");
            jasperOutputFormatMap.put("BIN", "BIN");
        }
        return jasperOutputFormatMap;
    }

    @SecurityControlIgnore
    public String getFormat() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String format = request.getParameter("format");
        //Jasper+AppletԤ��ģʽ����JasperPrint���������JRView������ʾ
        if (BooleanUtils.toBoolean(request.getParameter("preview"))) {
            format = "BIN";
        }
        if (StringUtils.isBlank(format) || !getJasperOutputFormatMap().containsKey(format)) {
            format = JasperReportConstants.FORMAT_PDF;
        }
        return format;
    }

    @SecurityControlIgnore
    public String getContentDisposition() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String contentDisposition = request.getParameter("contentDisposition");
        if (StringUtils.isBlank(contentDisposition)) {
            contentDisposition = "inline";
        }
        return contentDisposition;
    }

    @SecurityControlIgnore
    public String getDocumentName() {
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            String reportName = request.getParameter("report");
            return new String(reportName.getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            logger.warn("Chinese file name encoding error", e);
            return "export-data";
        }
    }

    /** JasperReport�������Map */
    private Map<String, Object> reportParameters = Maps.newHashMap();

    public void setReportParameters(Map<String, Object> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Map<String, Object> getReportParameters() {
        return reportParameters;
    }

    public Map<String, Object> getJasperReportParameters() {
        Map<String, Object> jasperReportParameters = Maps.newHashMap();
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            String reportId = request.getParameter("report");
            File targetJasperFile = getTargetJasperFile(reportId);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(targetJasperFile);

            //����ģ�嶨��ת���������������
            JRParameter[] params = jasperReport.getParameters();
            for (Map.Entry<String, Object> val : reportParameters.entrySet()) {
                String key = val.getKey();

                if (val.getValue() == null) {
                    continue;
                }
                String[] vals = (String[]) val.getValue();
                for (JRParameter param : params) {
                    if (!param.isSystemDefined() && param.isForPrompting()) {
                        String name = param.getName();
                        Class<?> clazz = param.getValueClass();
                        if (!name.equals(key)) {
                            continue;
                        }
                        //TODO: �ȳ�����Ӽ��ϴ���������������֡����ڵ�����ת������
                        if (Collection.class.isAssignableFrom(clazz)) {
                            //�������Ͳ�������TODO: ���Կ��ǽ�һ�����param.getNestedType()����
                            jasperReportParameters.put(key, vals);
                        } else {
                            //��������Ѳ���ת��Ϊ��ͨ�ַ�������
                            jasperReportParameters.put(val.getKey(), StringUtils.join(vals, ","));
                        }
                    }
                }
            }

            //����һЩȱʡ���Թ�ģ���ڲ�ʹ��
            jasperReportParameters.put("_RPT_ID", reportId);
            jasperReportParameters.put("_RPT_FORMAT", this.getFormat());
            String url = request.getRequestURL().toString();
            logger.debug("Report URL: " + url);
            jasperReportParameters.put("_RPT_URL", url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WebException(e.getMessage(), e);
        }
        return jasperReportParameters;
    }

    /** JasperReport�������Map */
    private Map<String, String> exportParameters = Maps.newHashMap();

    public void setExportParameters(Map<String, String> exportParameters) {
        this.exportParameters = exportParameters;
    }

    public Map<String, String> getExportParameters() {
        return exportParameters;
    }

    public Map<String, String> getJasperExportParameters() {
        try {
            String format = getFormat();
            if (JasperReportConstants.FORMAT_XLS.equals(format)) {
                HttpServletRequest request = ServletActionContext.getRequest();
                String reportId = request.getParameter("report");
                File targetJasperFile = getTargetJasperFile(reportId);
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(targetJasperFile);

                //Ϊ�˷���ģ����ƺ���������������չ�ṩһ��PROPERTY_WORKBOOK_TEMPLATE_FILENAME����
                //�����ģ���ļ��ж���˲���ֵ����ܻ��ô��ļ���ת��Ϊ��Ӧ��jasper�ļ�����ͬ·���ľ���·��ȥ����ģ����PROPERTY_WORKBOOK_TEMPLATE����ֵ
                JRPropertiesMap properties = jasperReport.getPropertiesMap();
                String xlsWorkbookTemplateFilename = properties.getProperty(PROPERTY_WORKBOOK_TEMPLATE_FILENAME);
                if (StringUtils.isNotBlank(xlsWorkbookTemplateFilename)) {
                    String xlsWorkbookTemplateFilepath = getWebRootDir() + getRelativeJasperFilePath() + File.separator
                            + xlsWorkbookTemplateFilename;
                    logger.debug("Overwrite jasper property: {}={}", JRXlsAbstractExporter.PROPERTY_WORKBOOK_TEMPLATE,
                            xlsWorkbookTemplateFilepath);
                    exportParameters.put(JRXlsAbstractExporter.PROPERTY_WORKBOOK_TEMPLATE, xlsWorkbookTemplateFilepath);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WebException(e.getMessage(), e);
        }
        return exportParameters;
    }

    public void prepareShow() {
        String report = this.getRequiredParameter("report");
        ReportDef reportDef = reportDefService.findByCode(report);
        setModel(reportDef);
    }

    @MetaData(value = "������ʾ")
    public HttpHeaders show() {
        return buildDefaultHttpHeaders("show");
    }

    /**
     * ��ѯ�����ֵ�����Ӧ���ݼ���Map
     * @param primaryKey �����ֵ�������
     * @return
     */
    public Map<String, String> getDataDictKeyValueMap(String primaryKey) {
        return dataDictService.findMapDataByPrimaryKey(primaryKey);
    }

    /**
     * ����SQL��ѯ��Ӧ���ݼ���Map
     * @param sql ����Key-Value��ʽ��SQL���
     * @return
     */
    public Map<String, String> getSQLKeyValueMap(String sql) {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();
        try {
            if (StringUtils.isNotBlank(sql)) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
                while (row.next()) {
                    dataMap.put(row.getString(1), row.getString(2));
                }
            }
        } catch (Exception e) {
            logger.error("SQL parse error: " + sql, e);
            dataMap.put("ERROR", "[ϵͳ��������쳣]");
        }
        return dataMap;
    }

    /**
     * ����Enum Class�ַ�����ȡ��Ӧ���ݼ���Map
     * @param enumClass Enum Class��Ӧ��������·��
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Map<String, String> getEnumKeyValueMap(String enumClass) {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();
        try {
            if (StringUtils.isNotBlank(enumClass)) {
                Class clazz = Class.forName(enumClass);
                for (Field enumfield : clazz.getFields()) {
                    MetaData entityComment = enumfield.getAnnotation(MetaData.class);
                    String value = enumfield.getName();
                    if (entityComment != null) {
                        value = entityComment.value();
                    }
                    dataMap.put(enumfield.getName(), value);
                }
            }
        } catch (Exception e) {
            //���ڴ��쳣������JSPҳ��������̣��޷�ת��ȫ�ֵ�errors������ʾҳ�棬��˲���logger��¼error��Ϣ
            logger.error("Enum parse error: " + enumClass, e);
            dataMap.put("ERROR", "[ϵͳ��������쳣]");
        }
        return dataMap;
    }

    /**
     * ��OGNL�ַ���ת��Ϊ��Ӧ���ݼ���Map
     * @param ognl OGNL�﷨�ַ������磺#{'A':'ClassA','B':'ClassB'}
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getOGNLKeyValueMap(String ognl) throws OgnlException {
        Map dataMap = new LinkedHashMap();
        try {
            if (StringUtils.isNotBlank(ognl)) {
                dataMap = (Map) Ognl.getValue(ognl, null);
            }
        } catch (Exception e) {
            //���ڴ��쳣������JSPҳ��������̣��޷�ת��ȫ�ֵ�errors������ʾҳ�棬��˲���logger��¼error��Ϣ
            logger.error("Ognl parse error: " + ognl, e);
            dataMap.put("ERROR", "[ϵͳ��������쳣]");
        }
        return dataMap;
    }
}
