/**
 * Copyright (c) 2012
 */
package com.etrade.framework.core.pagination;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import ognl.OgnlRuntime;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;
import org.joda.time.DateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

import com.etrade.framework.core.util.reflection.ConvertUtils;
import com.etrade.framework.core.web.convert.DateConverter;
import com.etrade.framework.core.web.util.ServletUtils;

/**
 * �����ORMʵ���޹ص����Թ���������װ��, ��Ҫ��¼ҳ���м򵥵�������������. ����ҳ��������ַ�����ʽ������Ȼ��ת������ΪDAO����ʶ���SQL����
 * ҳ���Ԫ��ʾ����
 * <ul>
 * <li>search['CN_a_OR_b']</li>
 * <li>search['EQ_id']</li>
 * <li>search['CN_user.name']</li>
 * </ul>
 * <p>
 * FORM���ݱ��������� <br/>
 * 1, ��һ���֣���"search[]"��Ϊ��ѯ������ʶ <br/>
 * 2, �ڶ����֣���ѯ���ͣ�@see #MatchType <br/>
 * 3, �������֣�id_OR_email��category��state, user.userprofileΪ��������,һ���Ӧ��Hibernate
 * Entity��Ӧ����,������_OR_�ָ�������Խ���OR��ѯ
 * </p>
 * <p>
 * ����ƴװ�ַ�����ʽ��Ҫ����JSPҳ��form��Ԫ��name����ֵ,�����Java�������׷�ӹ�������,һ��ֱ���ù��캯��:
 * PropertyFilter(final MatchType matchType, final String propertyName, final
 * Object matchValue)
 * </p>
 */
public class PropertyFilter {

    private static DateConverter dateConverter = new DateConverter();

    /** ������Լ�OR��ϵ�ķָ���. */
    public static final String OR_SEPARATOR = "_OR_";

    /** ����ƥ��Ƚ�����. */
    public enum MatchType {
        /** "name": "bk", "description": "is blank", "operator":"IS NULL OR ==''" */
        BK,

        /** "name": "nb", "description": "is not blank", "operator":"IS NOT NULL AND !=''" */
        NB,

        /** "name": "nu", "description": "is null", "operator":"IS NULL" */
        NU,

        /** "name": "nn", "description": "is not null", "operator":"IS NOT NULL" */
        NN,

        /** "name": "in", "description": "in", "operator":"IN" */
        IN,

        /** "name": "ni", "description": "not in", "operator":"NOT IN" */
        NI,

        /** "name": "ne", "description": "not equal", "operator":"<>" */
        NE,

        /** "name": "eq", "description": "equal", "operator":"=" */
        EQ,

        /** "name": "cn", "description": "contains", "operator":"LIKE %abc%" */
        CN,

        /**
         * "name": "nc", "description": "does not contain",
         * "operator":"NOT LIKE %abc%"
         */
        NC,

        /** "name": "bw", "description": "begins with", "operator":"LIKE abc%" */
        BW,

        /**
         * "name": "bn", "description": "does not begin with",
         * "operator":"NOT LIKE abc%"
         */
        BN,

        /** "name": "ew", "description": "ends with", "operator":"LIKE %abc" */
        EW,

        /**
         * "name": "en", "description": "does not end with",
         * "operator":"NOT LIKE %abc"
         */
        EN,

        /** "name": "bt", "description": "between", "operator":"BETWEEN 1 AND 2" */
        BT,

        /** "name": "lt", "description": "less", "operator":"С��" */
        LT,

        /** "name": "gt", "description": "greater", "operator":"����" */
        GT,

        /** "name": "le", "description": "less or equal","operator":"<=" */
        LE,

        /** "name": "ge", "description": "greater or equal", "operator":">=" */
        GE,

        /** @see javax.persistence.criteria.Fetch */
        FETCH,

        /** Property Less Equal: >= */
        PLE,

        /** Property Less Than: > */
        PLT,

        ACLPREFIXS;
    }

    /** ƥ������ */
    private MatchType matchType = null;

    /** ƥ��ֵ */
    private Object matchValue = null;

    /**
     * ƥ����������
     * ����˵��������Ƕ��������ȡ��һ��,�����Ҫȷ��_OR_��������������ͬһ����
     */
    private Class<?>  propertyClass = null;

    /** ������������, һ���ǵ�������,�����_OR_��Ϊ��� */
    private String[] propertyNames = null;
    /**
     * ���������Ӳ�ѯ,���ѯ����ĳ����Ʒ�����ж����б�,��order�����и�List����products�����������������:search['EQ_products.code'] 
     * ����˵�������ֻ֧�ֵ�ǰ������ֱ�Ӷ���ļ��϶��󼯺ϲ�ѯ����֧���ٶ��Ƕ��
     */
    private Class<?>  subQueryCollectionPropetyType;

    public PropertyFilter() {
    }

    /**
     * @param filterName
     *            �Ƚ������ַ���,�����ȽϵıȽ����͡�����ֵ���ͼ������б�.
     * @param values
     *            ���Ƚϵ�ֵ.
     */
    @SuppressWarnings("unchecked")
    public PropertyFilter(Class<?> entityClass, String filterName, String... values) {

        String matchTypeCode = StringUtils.substringBefore(filterName, "_");

        try {
            matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("filter����" + filterName + "û�а������д,�޷��õ����ԱȽ�����.", e);
        }

        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter����" + filterName
                                                               + "û�а������д,�޷��õ���������.");
        propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr, PropertyFilter.OR_SEPARATOR);
        try {
            if (propertyNameStr.indexOf("count(") > -1) {
                propertyClass = Integer.class;
            } else if (propertyNameStr.indexOf("(") > -1) {
                propertyClass = BigDecimal.class;
            } else {
                Method method = null;
                String[] namesSplits = StringUtils.split(propertyNames[0], ".");
                if (namesSplits.length == 1) {
                    method = OgnlRuntime.getGetMethod(null, entityClass, propertyNames[0]);
                } else {
                    Class<?> retClass = entityClass;
                    for (String nameSplit : namesSplits) {
                        method = OgnlRuntime.getGetMethod(null, retClass, nameSplit);
                        retClass = method.getReturnType();
                        if (Collection.class.isAssignableFrom(retClass)) {
                            Type genericReturnType = method.getGenericReturnType();
                            if (genericReturnType instanceof ParameterizedType) {
                                retClass = (Class<T>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                                subQueryCollectionPropetyType = retClass;
                            }
                        }
                    }
                }
                propertyClass = method.getReturnType();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("��Ч�������Զ��壺" + entityClass + ":" + propertyNames[0],
                e);
        }

        if (values.length == 1) {
            if (matchType.equals(MatchType.IN) || matchType.equals(MatchType.NI)) {
                String value = values[0];
                values = value.split(",");
            } else if (propertyClass.equals(Date.class) || propertyClass.equals(DateTime.class)) {
                String value = values[0];
                value = value.replace("��", " ");
                if (value.indexOf(" ") > -1) {
                    values = StringUtils.split(value, " ");
                    if (matchType.equals(MatchType.BT)) {
                        values[0] = values[0].trim();
                        values[1] = values[1].trim();
                    } else {
                        values = new String[] { values[0].trim() };
                    }
                }
            }
        }

        if (values.length == 1) {
            this.matchValue = parseMatchValueByClassType(propertyClass, values[0]);
        } else {
            Object[] matchValues = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                matchValues[i] = parseMatchValueByClassType(propertyClass, values[i]);
            }
            this.matchValue = matchValues;
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseMatchValueByClassType(@SuppressWarnings("rawtypes") Class propertyClass,
                                              String value) {
        if ("NULL".equalsIgnoreCase(value)) {
            return value;
        }
        if (Enum.class.isAssignableFrom(propertyClass)) {
            return Enum.valueOf(propertyClass, value);
        } else if (propertyClass.equals(Boolean.class) || matchType.equals(MatchType.NN)
                || matchType.equals(MatchType.NU)) {
            return new Boolean(BooleanUtils.toBoolean(value));
        } else if (propertyClass.equals(Date.class) || propertyClass.equals(DateTime.class)) {
            return dateConverter.convertValue(null, null, null, null, value, Date.class);
        } else {
            return ConvertUtils.convertStringToObject(value, propertyClass);
        }
    }

    /**
     * Java�����ֱ�ӹ������������, ��filters.add(new PropertyFilter(MatchType.EQ, "code",
     * code));
     * 
     * @param matchType
     * @param propertyName
     * @param matchValue
     */
    public PropertyFilter(final MatchType matchType, final String propertyName, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = StringUtils.splitByWholeSeparator(propertyName, PropertyFilter.OR_SEPARATOR);
        this.matchValue = matchValue;
    }

    /**
     * Java�����ֱ�ӹ������������, ��filters.add(new PropertyFilter(MatchType.LIKE, new
     * String[]{"code","name"}, value));
     * 
     * @param matchType
     * @param propertyName
     * @param matchValue
     */
    public PropertyFilter(final MatchType matchType, final String[] propertyNames, final Object matchValue) {
        this.matchType = matchType;
        this.propertyNames = propertyNames;
        this.matchValue = matchValue;
    }

    /**
     * ��HttpRequest�д���PropertyFilter�б�
     * PropertyFilter��������ΪFilter����ǰ׺_�Ƚ�������������_������.
     */
    public static List<PropertyFilter> buildFiltersFromHttpRequest(Class<?> entityClass, ServletRequest request) {

        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        // ��request�л�ȡ������ǰ׺���Ĳ���,����ȥ��ǰ׺����Ĳ���Map.
        Map<String, String[]> filterParamMap = ServletUtils.getParametersStartingWith(request, "search['", "']");

        // ��������Map,����PropertyFilter�б�
        for (Map.Entry<String, String[]> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                continue;
            }

            if (values.length == 1) {
                String value = values[0];
                // ���valueֵΪ��,����Դ�filter.
                if (StringUtils.isNotBlank(value)) {
                    PropertyFilter filter = new PropertyFilter(entityClass, filterName, value);
                    filterList.add(filter);
                }
            } else {
                String[] valuesArr = values;
                // ���valueֵΪ��,����Դ�filter.
                if (valuesArr.length > 0) {
                    Set<String> valueSet = new HashSet<String>();
                    for (String value : valuesArr) {
                        if (StringUtils.isNotBlank(value)) {
                            valueSet.add(value);
                        }
                    }
                    if (valueSet.size() > 0) {
                        String[] realValues = new String[valueSet.size()];
                        int cnt = 0;
                        for (String v : valueSet) {
                            realValues[cnt++] = v;
                        }
                        PropertyFilter filter = new PropertyFilter(entityClass, filterName, realValues);
                        filterList.add(filter);
                    }

                }
            }

        }
        return filterList;
    }

    public static Pageable buildPageableFromHttpRequest(HttpServletRequest request) {
        String rows = StringUtils.isBlank(request.getParameter("rows")) ? "10" : request.getParameter("rows");
        if (Integer.valueOf(rows) < 0) {
            return null;
        }
        String page = StringUtils.isBlank(request.getParameter("page")) ? "1" : request.getParameter("page");
        Sort sort = buildSortFromHttpRequest(request);
        return new PageRequest(Integer.valueOf(page) - 1, Integer.valueOf(rows), sort);
    }

    public static Sort buildSortFromHttpRequest(HttpServletRequest request) {
        String sidx = StringUtils.isBlank(request.getParameter("sidx")) ? "id" : request.getParameter("sidx");
        Direction sord = "desc".equalsIgnoreCase(request.getParameter("sord")) ? Direction.DESC : Direction.ASC;
        Sort sort = null;

        //���ն����з�֧�ֶ���������
        for (String sidxItem : sidx.split(",")) {
            if (StringUtils.isNotBlank(sidxItem)) {
                //�ٰ��ո��зֻ�ȡ�������Ժ�������
                String[] sidxItemWithOrder = sidxItem.trim().split(" ");
                String sortname = sidxItemWithOrder[0];
                //�����ѯ���԰���_OR_��ȡ��һ����Ϊ��������
                //�����дOR�����Բ�ѯʱע�����������д����ǰ��
                if (sortname.indexOf(OR_SEPARATOR) > -1) {
                    sortname = StringUtils.substringBefore(sortname, OR_SEPARATOR);
                }
                //�����������û�и�����������ȡGrid��������sord��������
                if (sidxItemWithOrder.length == 1) {
                    if (sort == null) {
                        //��ʼ���������
                        sort = new Sort(sord, sortname);
                    } else {
                        //and׷�Ӷ������
                        sort = sort.and(new Sort(sord, sortname));
                    }
                } else {
                    //�������Ժ���ո������������
                    String sortorder = sidxItemWithOrder[1];
                    if (sort == null) {
                        sort = new Sort("desc".equalsIgnoreCase(sortorder) ? Direction.DESC : Direction.ASC, sortname);
                    } else {
                        sort = sort.and(new Sort("desc".equalsIgnoreCase(sortorder) ? Direction.DESC : Direction.ASC,
                                sortname));
                    }
                }
            }
        }
        return sort;
    }

    /**
     * ��ȡ�ȽϷ�ʽ.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * ��ȡ�Ƚ�ֵ.
     */
    public Object getMatchValue() {
        return matchValue;
    }

    /**
     * ��ȡ�Ƚ����������б�.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * ��ȡΨһ�ıȽ���������.
     */
    public String getPropertyName() {
        Assert.isTrue(propertyNames.length == 1, "There are not only one property in this filter.");
        return propertyNames[0];
    }

    /**
     * �Ƿ�Ƚ϶������.
     */
    public boolean hasMultiProperties() {
        return (propertyNames.length > 1);
    }

    /**
     * ����һ��ȱʡ���˼���.
     */
    public static List<PropertyFilter> buildDefaultFilterList() {
        return new ArrayList<PropertyFilter>();
    }

    @SuppressWarnings("rawtypes")
    public Class getPropertyClass() {
        return propertyClass;
    }

    @SuppressWarnings("rawtypes")
    public Class getSubQueryCollectionPropetyType() {
        return subQueryCollectionPropetyType;
    }
}
