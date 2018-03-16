package com.etrade.framework.core.profile.web.action;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.profile.entity.ProfileParamVal;
import com.etrade.framework.core.profile.service.ProfileParamValService;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.service.DataDictService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;

@MetaData("���Ի����ò������ݹ���")
public class ProfileParamValController extends BaseController<ProfileParamVal, String> {

    private final Logger logger = LoggerFactory.getLogger(ProfileParamValController.class);

    @Autowired
    private ProfileParamValService profileParamValService;

    @Autowired
    private DataDictService dataDictService;

    @Autowired
    private DataSource dataSource;

    @Override
    protected BaseService<ProfileParamVal, String> getEntityService() {
        return profileParamValService;
    }

    @Override
    protected void checkEntityAclPermission(ProfileParamVal entity) {
        // TODO Add acl check code logic
    }

    @MetaData("[TODO��������]")
    public HttpHeaders todo() {
        //TODO
        setModel(OperationResult.buildSuccessResult("TODO�������"));
        return buildDefaultHttpHeaders();
    }

    @Override
    public void prepareEdit() {

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

    /**
    * ��ѯ�����ֵ�����Ӧ���ݼ���Map
    * @param category �����ֵ�������
    * @return
    */
    public Map<String, String> getDataDictKeyValueMap(String category) {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();
        try {
            dataMap = dataDictService.findMapDataByPrimaryKey(category);
        } catch (Exception e) {
            logger.error("DataDict parse error: " + category, e);
            dataMap.put("ERROR", "[ϵͳ��������쳣]");
        }
        return dataMap;
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