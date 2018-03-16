package com.etrade.framework.core.pub.web.action;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.RestActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ����������
 */
public class GridController extends RestActionSupport {

    /**  */
    private static final long   serialVersionUID = 1L;
    private final static Logger logger = LoggerFactory.getLogger(GridController.class);

    public void export() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        String filename = request.getParameter("fileName");
        filename = new String(filename.getBytes("GBK"), "ISO-8859-1");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        String exportDatas = request.getParameter("exportDatas");
        OutputStream os = response.getOutputStream();

        HSSFWorkbook wb = new HSSFWorkbook();//����Excel����������   
        HSSFSheet sheet = wb.createSheet(filename);//����Excel���������     
        String[] rows = exportDatas.split("\n");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            if (StringUtils.isNotBlank(row)) {
                logger.trace("Row {}: {}", i, row);
                // ����Excel��sheet��һ��
                HSSFRow hssfRow = sheet.createRow(i);
                String[] cells = row.split("\t");
                for (int j = 0; j < cells.length; j++) {
                    String cell = cells[j];
                    // ����һ��Excel�ĵ�Ԫ��
                    HSSFCell hssfCell = hssfRow.createCell(j);
                    hssfCell.setCellValue(cell);
                }
            }
        }
        wb.write(os);
        IOUtils.closeQuietly(os);
    }
}
