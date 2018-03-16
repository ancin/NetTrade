package com.etrade.framework.core.pub.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.common.SimpleController;
import com.etrade.framework.core.sys.entity.DataDict;
import com.etrade.framework.core.sys.service.DataDictService;
import com.etrade.framework.core.web.listener.ApplicationContextPostListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 公共数据服务处理
 */
public class DataController extends SimpleController {

    @Autowired
    private DataDictService dataDictService;

    /**
     * @see ApplicationContextPostListener
     * @return
     */
    @MetaData("枚举数据集合")
    public HttpHeaders enums() {
        ServletContext sc = ServletActionContext.getServletContext();
        setModel(sc.getAttribute("enums"));
        return buildDefaultHttpHeaders();
    }

    /**
     * @see ApplicationContextPostListener
     * @return
     */
    @MetaData("数据字典数据集合")
    public HttpHeaders dictDatas() {
        List<Map<String, Object>> datas = Lists.newArrayList();
        List<DataDict> dataDicts = dataDictService.findAllCached();
        for (DataDict dataDict : dataDicts) {
            Map<String, Object> data = Maps.newHashMap();
            data.put("primaryKey", dataDict.getPrimaryKey());
            data.put("primaryValue", dataDict.getPrimaryValue());
            DataDict parent = dataDict.getParent();
            if (parent != null) {
                data.put("parentPrimaryKey", parent.getPrimaryKey());
            }
            datas.add(data);
        }
        setModel(datas);
        return buildDefaultHttpHeaders();
    }
}
