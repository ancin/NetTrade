package com.etrade.framework.core.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.audit.envers.EntityRevision;
import com.etrade.framework.core.audit.envers.ExtDefaultRevisionEntity;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.exception.ServiceException;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Transactional
public abstract class BaseService<T extends Persistable<? extends Serializable>, ID extends Serializable> {

    private final Logger logger = LoggerFactory.getLogger(BaseService.class);

    /** ���Ͷ�Ӧ��Class���� */
    protected Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    /** �������þ����DAO����ʵ�� */
    abstract protected BaseDao<T, ID> getEntityDao();

    @SuppressWarnings("unchecked")
    public BaseService() {
        super();
        // ͨ������ȡ��Entity��Class.
        try {
            Object genericClz = getClass().getGenericSuperclass();
            if (genericClz instanceof ParameterizedType) {
                entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
            }
        } catch (Exception e) {
            logger.error("error detail:", e);
        }
    }

    /**
     * �������ݱ�������֮ǰ��������ص����� Ĭ��Ϊ���߼������������Ҫ��д����߼�����
     * 
     * @param entity
     *            ���������ݶ���
     */
    protected void preInsert(T entity) {

    }

    /**
     * �������ݱ�������֮ǰ��������ص����� Ĭ��Ϊ���߼������������Ҫ��д����߼�����
     * 
     * @param entity
     *            ���������ݶ���
     */
    protected void preUpdate(T entity) {

    }

    /**
     * ���ݱ������
     * 
     * @param entity
     * @return
     */
    public T save(T entity) {
        if (entity.isNew()) {
            preInsert(entity);
        } else {
            preUpdate(entity);
        }
        return getEntityDao().save(entity);
    }

    /**
     * �������ݱ������ ��ʵ��ֻ�Ǽ�ѭ������ÿ��Ԫ�ص��� {@link #save(Persistable)}
     * ��˲���ʵ�ʵ�Batch�������������Ҫ���ݿ�ײ�����֧��������ʵ��
     * 
     * @param entities
     *            �������������ݼ���
     * @return
     */
    public List<T> save(Iterable<T> entities) {
        List<T> result = new ArrayList<T>();
        if (entities == null) {
            return result;
        }
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    /**
     * ����������ѯ��һ���ݶ���
     * 
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public T findOne(ID id) {
        Assert.notNull(id);
        return getEntityDao().findOne(id);
    }

    /**
     * ����������ѯ��һ���ݶ���
     * 
     * @param id ����
     * @param initLazyPropertyNames ��ҪԤ�ȳ�ʼ����lazy������������
     * @return
     */
    @Transactional(readOnly = true)
    public T findDetachedOne(ID id, String... initLazyPropertyNames) {
        Assert.notNull(id);
        T entity = getEntityDao().findOne(id);
        if (initLazyPropertyNames != null && initLazyPropertyNames.length > 0) {
            for (String name : initLazyPropertyNames) {
                try {
                    Object propValue = MethodUtils.invokeMethod(entity, "get" + StringUtils.capitalize(name));
                    if (propValue != null && propValue instanceof Collection<?>) {
                        ((Collection<?>) propValue).size();
                    } else if (propValue != null && propValue instanceof Persistable<?>) {
                        ((Persistable<?>) propValue).getId();
                    }
                } catch (Exception e) {
                    throw new ServiceException("error.init.detached.entity", e);
                }
            }
        }
        entityManager.detach(entity);
        return entity;
    }

    /**
     * �����������ϲ�ѯ�������ݶ���
     * 
     * @param ids ��������
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> findAll(final ID... ids) {
        Assert.isTrue(ids != null && ids.length > 0, "�����ṩ��Ч��ѯ��������");
        Specification<T> spec = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                @SuppressWarnings("rawtypes")
                Path expression = root.get("id");
                return expression.in(ids);
            }
        };
        return this.getEntityDao().findAll(spec);
    }

    /**
     * ����ɾ������
     * 
     * @param entity
     *            ����������
     */
    public void delete(T entity) {
        getEntityDao().delete(entity);
    }

    /**
     * ��������ɾ������ ��ʵ��ֻ�Ǽ�ѭ������ÿ��Ԫ�ص��� {@link #delete(Persistable)}
     * ��˲���ʵ�ʵ�Batch�������������Ҫ���ݿ�ײ�����֧��������ʵ��
     * 
     * @param entities
     *            �������������ݼ���
     * @return
     */
    public void delete(Iterable<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    /**
     * ���ݷ��Ͷ������Ժ�ֵ��ѯΨһ����
     * 
     * @param property ����������������������������
     * @param value ����ֵ
     * @return δ��ѯ������null�������ѯ�������������׳��쳣
     */
    public T findByProperty(final String property, final Object value) {
        Specification<T> spec = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                @SuppressWarnings("rawtypes")
                Path expression = root.get(property);
                return builder.equal(expression, value);
            }
        };

        List<T> entities = this.getEntityDao().findAll(spec);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        } else {
            Assert.isTrue(entities.size() == 1);
            return entities.get(0);
        }
    }

    /**
     * ���ݷ��Ͷ������Ժ�ֵ��ѯΨһ����
     * 
     * @param property ����������������������������
     * @param value ����ֵ
     * @return δ��ѯ������null�������ѯ�����������򷵻ص�һ��
     */
    public T findFirstByProperty(final String property, final Object value) {
        Specification<T> spec = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                @SuppressWarnings("rawtypes")
                Path expression = root.get(property);
                return builder.equal(expression, value);
            }
        };

        List<T> entities = this.getEntityDao().findAll(spec);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        } else {
            return entities.get(0);
        }
    }

    /**
     * ͨ�õĶ������Ժ�ֵ��ѯ�ӿڣ����ݷ��Ͳ���ȷ��������������
     * 
     * @param baseDao
     *            ���Ͳ�������DAO�ӿ�
     * @param property
     *            ����������������������������
     * @param value
     *            ����ֵ
     * @return δ��ѯ������null�������ѯ�������������׳��쳣
     */
    public <X> X findByProperty(BaseDao<X, ID> baseDao, final String property, final Object value) {
        Specification<X> spec = new Specification<X>() {
            @Override
            public Predicate toPredicate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                String[] names = StringUtils.split(property, ".");
                @SuppressWarnings("rawtypes")
                Path expression = root.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    expression = expression.get(names[i]);
                }
                return builder.equal(expression, value);
            }
        };
        List<X> entities = baseDao.findAll(spec);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        } else {
            Assert.isTrue(entities.size() == 1);
            return entities.get(0);
        }
    }

    /**
     * ��һ���������ѯ���ݼ���
     * 
     * @param propertyFilter
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> findByFilter(PropertyFilter propertyFilter) {
        GroupPropertyFilter groupPropertyFilter = GroupPropertyFilter.buildDefaultAndGroupFilter(propertyFilter);
        Specification<T> spec = buildSpecification(groupPropertyFilter);
        return getEntityDao().findAll(spec);
    }

    /**
     * ���ڲ�ѯ����count��¼����
     * 
     * @param groupPropertyFilter
     * @return
     */
    @Transactional(readOnly = true)
    public long count(GroupPropertyFilter groupPropertyFilter) {
        Specification<T> spec = buildSpecification(groupPropertyFilter);
        return getEntityDao().count(spec);
    }

    /**
     * ���ڶ�̬������������ѯ���ݼ���
     * 
     * @param groupPropertyFilter
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> findByFilters(GroupPropertyFilter groupPropertyFilter) {
        Specification<T> spec = buildSpecification(groupPropertyFilter);
        return getEntityDao().findAll(spec);
    }

    /**
     * ���ڶ�̬�������������������ѯ���ݼ���
     * 
     * @param groupPropertyFilter
     * @param sort
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> findByFilters(GroupPropertyFilter groupPropertyFilter, Sort sort) {
        Specification<T> spec = buildSpecification(groupPropertyFilter);
        return getEntityDao().findAll(spec, sort);
    }

    @Transactional(readOnly = true)
    public <X extends Persistable> List<X> findByFilters(Class<X> clazz, GroupPropertyFilter groupPropertyFilter,
            Sort sort) {
        Specification<X> spec = buildSpecification(groupPropertyFilter);
        return ((BaseDao) spec).findAll(spec, sort);
    }

    /**
    * ���ڶ�̬�����������������壬���Ʋ�ѯ����ѯ���ݼ���
    * ��Ҫ����Autocomplete�����Ĳ�ѯ���ⷵ��̫������
    * @param groupPropertyFilter
    * @param sort
    * @return
    */
    @Transactional(readOnly = true)
    public List<T> findByFilters(GroupPropertyFilter groupPropertyFilter, Sort sort, int limit) {
        Pageable pageable = new PageRequest(0, limit, sort);
        return findByPage(groupPropertyFilter, pageable).getContent();
    }

    /**
     * ���ڶ�̬�����������ͷ�ҳ(������)�����ѯ���ݼ���
     * 
     * @param groupPropertyFilter
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<T> findByPage(GroupPropertyFilter groupPropertyFilter, Pageable pageable) {
        Specification<T> specifications = buildSpecification(groupPropertyFilter);
        return getEntityDao().findAll(specifications, pageable);
    }

    public String toSql(Criteria criteria) {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        SessionImplementor session = criteriaImpl.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator = new CriteriaQueryTranslator(factory, criteriaImpl,
                criteriaImpl.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());

        CriteriaJoinWalker walker = new CriteriaJoinWalker(
                (OuterJoinLoadable) factory.getEntityPersister(implementors[0]), translator, factory, criteriaImpl,
                criteriaImpl.getEntityOrClassName(), session.getLoadQueryInfluencers());

        String sql = walker.getSQLString();
        return sql;
    }

    private class GroupAggregateProperty {
        @MetaData(value = "��������", comments = "�������ǰ��JSON�����key")
        private String label;
        @MetaData(value = "JPA���ʽ", comments = "����JPA CriteriaBuilder��װ������")
        private String name;
        @MetaData(value = "JPA���ʽalias", comments = "���ڻ�ȡ�ۺ�ֵ�ı���")
        private String alias;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

    }

    /**
     * ����ۺ�ͳ�ƣ����������ư�����ʱ���ͳ����Ʒ�������󣬰���ƿ�Ŀ����ͳ�Ƶ�
     * 
     * @param clazz  ROOTʵ������
     * @param groupFilter ���˲�������
     * @param pageable ��ҳ�����������TODO��Ŀǰ�и�����δʵ���ܼ�¼������ֱ�ӷ���һ���̶�������
     * @param properties ���Լ��ϣ��жϹ����������ư���"("���ʶΪ�ۺ����ԣ�����Ϊ�������� 
     * �����﷨����sum = + , diff = - , prod = * , quot = / , case(condition,when,else)
     * ʾ����
     *     sum(amount)
     *     sum(diff(amount,costAmount))
     *     min(case(equal(amount,0),-1,quot(diff(amount,costAmount),amount)))
     *     case(equal(sum(amount),0),-1,quot(sum(diff(amount,costAmount)),sum(amount)))
     * @return Map�ṹ�ļ��Ϸ�ҳ����
     */
    public Page<Map<String, Object>> findByGroupAggregate(Class clazz, GroupPropertyFilter groupFilter,
            Pageable pageable, String... properties) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<?> root = criteriaQuery.from(clazz);

        //��������;ۺ������飬���Ƿ���ڡ�(����Ϊ��ʶ
        List<GroupAggregateProperty> groupProperties = Lists.newArrayList();
        List<GroupAggregateProperty> aggregateProperties = Lists.newArrayList();
        for (String prop : properties) {
            GroupAggregateProperty groupAggregateProperty = new GroupAggregateProperty();
            //�ۺ����ͱ��ʽ
            if (prop.indexOf("(") > -1) {
                //����as����
                prop = prop.replace(" AS ", " as ").replace(" As ", " as ").replace(" aS ", " as ");
                String[] splits = prop.split(" as ");
                String alias = null;
                String name = null;
                if (splits.length > 1) {
                    name = splits[0].trim();
                    alias = splits[1].trim();
                    groupAggregateProperty.setAlias(alias);
                    groupAggregateProperty.setLabel(alias);
                    groupAggregateProperty.setName(name);
                } else {
                    name = splits[0].trim();
                    alias = fixCleanAlias(name);
                    groupAggregateProperty.setAlias(alias);
                    groupAggregateProperty.setLabel(name);
                    groupAggregateProperty.setName(name);
                }
                aggregateProperties.add(groupAggregateProperty);
            } else {
                //ֱ�ӵ����Ա��ʽ
                groupAggregateProperty.setAlias(fixCleanAlias(prop));
                groupAggregateProperty.setLabel(prop);
                groupAggregateProperty.setName(prop);
                groupProperties.add(groupAggregateProperty);
            }
        }

        //����JPA Expression
        Expression<?>[] groupExpressions = buildExpressions(root, criteriaBuilder, groupProperties);
        Expression<?>[] aggregateExpressions = buildExpressions(root, criteriaBuilder, aggregateProperties);
        Expression<?>[] selectExpressions = ArrayUtils.addAll(groupExpressions, aggregateExpressions);
        CriteriaQuery<Tuple> select = criteriaQuery.multiselect(selectExpressions);

        //����ǰ�˶�̬��������̬where������װ
        Predicate where = buildPredicatesFromFilters(groupFilter, root, criteriaQuery, criteriaBuilder, false);
        if (where != null) {
            select.where(where);
        }
        //����ǰ�˶�̬��������̬having������װ
        Predicate having = buildPredicatesFromFilters(groupFilter, root, criteriaQuery, criteriaBuilder, true);
        if (having != null) {
            select.having(having);
        }

        //��ҳ��������
        if (pageable != null && pageable.getSort() != null) {
            Iterator<Order> orders = pageable.getSort().iterator();
            List<javax.persistence.criteria.Order> jpaOrders = Lists.newArrayList();
            while (orders.hasNext()) {
                Order order = orders.next();
                String prop = order.getProperty();
                String alias = fixCleanAlias(prop);
                //Ŀǰ����JPA��֧�ִ���alias��Ϊ�������ԣ����ֻ�ܻ���alias�ҵ�ƥ���Expression���ʽ��Ϊ�������
                List<Selection<?>> selections = select.getSelection().getCompoundSelectionItems();
                for (Selection<?> selection : selections) {
                    if (selection.getAlias().equals(alias)) {
                        if (order.isAscending()) {
                            jpaOrders.add(criteriaBuilder.asc((Expression<?>) selection));
                        } else {
                            jpaOrders.add(criteriaBuilder.desc((Expression<?>) selection));
                        }
                        break;
                    }
                }
            }
            select.orderBy(jpaOrders);
        }

        //׷�ӷ������
        select.groupBy(groupExpressions);

        //������ѯ����
        TypedQuery<Tuple> query = entityManager.createQuery(select);
        //��̬׷�ӷ�ҳ����
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        //��ȡ������ϣ�����װΪǰ�˱���JSON���л���Map�ṹ
        List<Tuple> tuples = query.getResultList();
        List<Map<String, Object>> mapDatas = Lists.newArrayList();
        for (Tuple tuple : tuples) {
            Map<String, Object> data = Maps.newHashMap();
            for (GroupAggregateProperty groupAggregateProperty : groupProperties) {
                data.put(groupAggregateProperty.getLabel(), tuple.get(groupAggregateProperty.getAlias()));
            }
            for (GroupAggregateProperty groupAggregateProperty : aggregateProperties) {
                data.put(groupAggregateProperty.getLabel(), tuple.get(groupAggregateProperty.getAlias()));
            }
            mapDatas.add(data);
        }

        //TODO��Ŀǰ�и�����δʵ���ܼ�¼������ֱ�ӷ���һ���̶�������
        return new PageImpl(mapDatas, pageable, Integer.MAX_VALUE);
    }

    /**
     * ���ڵ�ǰ����ʵ��������ͣ����÷���ͳ�ƽӿ�
     * @param groupFilter
     * @param pageable
     * @param properties
     * @return
     */
    public Page<Map<String, Object>> findByGroupAggregate(GroupPropertyFilter groupFilter, Pageable pageable,
            String... properties) {
        return findByGroupAggregate(entityClass, groupFilter, pageable, properties);
    }

    /**
     * ����Native SQL�ͷ�ҳ(������������ֱ����native sql�ж���)�����ѯ���ݼ���
     * 
     * @param pageable ��ҳ(������������ֱ����native sql�ж���)����
     * @param sql Native SQL(������װ�ö�̬�����������ԭ��SQL��䣬����order by����)
     * @return Map�ṹ�ļ��Ϸ�ҳ����
     */
    @Transactional(readOnly = true)
    public Page<Map> findByPageNativeSQL(Pageable pageable, String sql) {
        return findByPageNativeSQL(pageable, sql, null);
    }

    /**
     * ����Native SQL�ͷ�ҳ(������������ֱ����native sql�ж���)�����ѯ���ݼ���
     * 
     * @param pageable ��ҳ(������������ֱ����native sql�ж���)����
     * @param sql Native SQL(������װ�ö�̬�����������ԭ��SQL��䣬����order by����)
     * @param orderby order by����
     * @return Map�ṹ�ļ��Ϸ�ҳ����
     */
    @Transactional(readOnly = true)
    public Page<Map> findByPageNativeSQL(Pageable pageable, String sql, String orderby) {
        Query query = null;
        if (StringUtils.isNotBlank(orderby)) {
            query = entityManager.createNativeQuery(sql + " " + orderby);
        } else {
            query = entityManager.createNativeQuery(sql);
        }
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Query queryCount = entityManager.createNativeQuery("select count(*) from (" + sql + ") cnt");
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        Object count = queryCount.getSingleResult();
        return new PageImpl(query.getResultList(), pageable, Long.valueOf(count.toString()));
    }

    /**
     * ����JPAͨ�õĲ�ѯ����count��¼����
     * 
     * @param spec
     * @return
     */
    @Transactional(readOnly = true)
    public long count(Specification<T> spec) {
        return getEntityDao().count(spec);
    }

    @SuppressWarnings("unchecked")
    private <X> Predicate buildPredicate(String propertyName, PropertyFilter filter, Root<X> root,
            CriteriaQuery<?> query, CriteriaBuilder builder, Boolean having) {
        Object matchValue = filter.getMatchValue();
        String[] names = StringUtils.split(propertyName, ".");

        if (matchValue == null) {
            return null;
        }
        if (having && propertyName.indexOf("(") == -1) {
            return null;
        }
        if (!having && propertyName.indexOf("(") > -1) {
            return null;
        }
        if (matchValue instanceof String) {
            if (StringUtils.isBlank(String.valueOf(matchValue))) {
                return null;
            }
        }

        if (filter.getMatchType().equals(MatchType.FETCH)) {
            if (names.length == 1) {
                JoinType joinType = JoinType.INNER;
                if (matchValue instanceof String) {
                    joinType = Enum.valueOf(JoinType.class, (String) matchValue);
                } else {
                    joinType = (JoinType) filter.getMatchValue();
                }

                // Hack for Bug: https://jira.springsource.org/browse/DATAJPA-105
                // �������count�����ܼ�¼�������join������˵��������ҳ��ѯ���fetch
                if (!Long.class.isAssignableFrom(query.getResultType())) {
                    root.fetch(names[0], joinType);
                } else {
                    root.join(names[0], joinType);
                }
            } else {
                JoinType[] joinTypes = new JoinType[names.length];
                if (matchValue instanceof String) {
                    String[] joinTypeSplits = StringUtils.split(String.valueOf(matchValue), ".");
                    Assert.isTrue(joinTypeSplits.length == names.length, filter.getMatchType()
                                                                         + " �������Ը�����Join������������һ��");
                    for (int i = 0; i < joinTypeSplits.length; i++) {
                        joinTypes[i] = Enum.valueOf(JoinType.class, joinTypeSplits[i].trim());
                    }
                } else {
                    joinTypes = (JoinType[]) filter.getMatchValue();
                    Assert.isTrue(joinTypes.length == names.length);
                }

                // Hack for Bug: https://jira.springsource.org/browse/DATAJPA-105
                // �������count�����ܼ�¼�������join������˵��������ҳ��ѯ���fetch
                if (!Long.class.isAssignableFrom(query.getResultType())) {
                    Fetch fetch = root.fetch(names[0], joinTypes[0]);
                    for (int i = 1; i < names.length; i++) {
                        fetch.fetch(names[i], joinTypes[i]);
                    }
                } else {
                    Join join = root.join(names[0], joinTypes[0]);
                    for (int i = 1; i < names.length; i++) {
                        join.join(names[i], joinTypes[i]);
                    }
                }
            }

            return null;
        }

        Predicate predicate = null;
        Expression expression = null;

        // �������Ӳ�ѯ
        Subquery<Long> subquery = null;
        Root subQueryFrom = null;
        if (filter.getSubQueryCollectionPropetyType() != null) {
            subquery = query.subquery(Long.class);
            subQueryFrom = subquery.from(filter.getSubQueryCollectionPropetyType());
            Path path = subQueryFrom.get(names[1]);
            if (names.length > 2) {
                for (int i = 2; i < names.length; i++) {
                    path = path.get(names[i]);
                }
            }
            expression = path;
        } else {
            expression = buildExpression(root, builder, propertyName, null);
        }

        if ("NULL".equalsIgnoreCase(String.valueOf(matchValue))) {
            return expression.isNull();
        } else if ("EMPTY".equalsIgnoreCase(String.valueOf(matchValue))) {
            return builder.or(builder.isNull(expression), builder.equal(expression, ""));
        } else if ("NONULL".equalsIgnoreCase(String.valueOf(matchValue))) {
            return expression.isNotNull();
        } else if ("NOEMPTY".equalsIgnoreCase(String.valueOf(matchValue))) {
            return builder.and(builder.isNotNull(expression), builder.notEqual(expression, ""));
        }

        // logic operator
        switch (filter.getMatchType()) {
        case EQ:
                // ���������⴦��һ�������������ڵĽ���ʱ���ѯ,���ѯ2012-01-01֮ǰ,һ����Ҫ��ʾ2010-01-01���켰��ǰ������,
                // �����ݿ�һ�����ʱ����,�����Ҫ���⴦��ѵ�ǰ����+1��,ת��Ϊ<2012-01-02���в�ѯ
            if (matchValue instanceof Date) {
                DateTime dateTime = new DateTime(((Date) matchValue).getTime());
                if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0
                        && dateTime.getSecondOfMinute() == 0) {
                    return builder.and(builder.greaterThanOrEqualTo(expression, dateTime.toDate()),
                            builder.lessThan(expression, dateTime.plusDays(1).toDate()));
                }
            }
            predicate = builder.equal(expression, matchValue);
            break;
        case NE:
                // ���������⴦��һ�������������ڵĽ���ʱ���ѯ,���ѯ2012-01-01֮ǰ,һ����Ҫ��ʾ2010-01-01���켰��ǰ������,
                // �����ݿ�һ�����ʱ����,�����Ҫ���⴦��ѵ�ǰ����+1��,ת��Ϊ<2012-01-02���в�ѯ
            if (matchValue instanceof Date) {
                DateTime dateTime = new DateTime(((Date) matchValue).getTime());
                if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0
                        && dateTime.getSecondOfMinute() == 0) {
                    return builder.or(builder.lessThan(expression, dateTime.toDate()),
                            builder.greaterThanOrEqualTo(expression, dateTime.plusDays(1).toDate()));
                }
            }
            predicate = builder.notEqual(expression, matchValue);
            break;
        case BK:
            predicate = builder.or(builder.isNull(expression), builder.equal(expression, ""));
            break;
        case NB:
            predicate = builder.and(builder.isNotNull(expression), builder.notEqual(expression, ""));
            break;
        case NU:
            if (matchValue instanceof Boolean && (Boolean) matchValue == false) {
                predicate = builder.isNotNull(expression);
            } else {
                predicate = builder.isNull(expression);
            }
            break;
        case NN:
            if (matchValue instanceof Boolean && (Boolean) matchValue == false) {
                predicate = builder.isNull(expression);
            } else {
                predicate = builder.isNotNull(expression);
            }
            break;
        case CN:
            predicate = builder.like(expression, "%" + matchValue + "%");
            break;
        case NC:
            predicate = builder.notLike(expression, "%" + matchValue + "%");
            break;
        case BW:
            predicate = builder.like(expression, matchValue + "%");
            break;
        case BN:
            predicate = builder.notLike(expression, matchValue + "%");
            break;
        case EW:
            predicate = builder.like(expression, "%" + matchValue);
            break;
        case EN:
            predicate = builder.notLike(expression, "%" + matchValue);
            break;
        case BT:
            Assert.isTrue(matchValue.getClass().isArray(), "Match value must be array");
            Object[] matchValues = (Object[]) matchValue;
            Assert.isTrue(matchValues.length == 2, "Match value must have two value");
                // ���������⴦��һ�������������ڵĽ���ʱ���ѯ,���ѯ2012-01-01֮ǰ,һ����Ҫ��ʾ2010-01-01���켰��ǰ������,
                // �����ݿ�һ�����ʱ����,�����Ҫ���⴦��ѵ�ǰ����+1��,ת��Ϊ<2012-01-02���в�ѯ
            if (matchValues[0] instanceof Date) {
                DateTime dateFrom = new DateTime(((Date) matchValues[0]).getTime());
                DateTime dateTo = new DateTime(((Date) matchValues[1]).getTime());
                if (dateFrom.getHourOfDay() == 0 && dateFrom.getMinuteOfHour() == 0
                        && dateFrom.getSecondOfMinute() == 0) {
                    return builder.and(builder.greaterThanOrEqualTo(expression, dateFrom.toDate()),
                            builder.lessThan(expression, dateTo.plusDays(1).toDate()));

                }
            } else {
                return builder.between(expression, (Comparable) matchValues[0], (Comparable) matchValues[1]);
            }
            predicate = builder.equal(expression, matchValue);
            break;
        case GT:
            predicate = builder.greaterThan(expression, (Comparable) matchValue);
            break;
        case GE:
            predicate = builder.greaterThanOrEqualTo(expression, (Comparable) matchValue);
            break;
        case LT:
                // ���������⴦��һ�������������ڵĽ���ʱ���ѯ,���ѯ2012-01-01֮ǰ,һ����Ҫ��ʾ2010-01-01���켰��ǰ������,
                // �����ݿ�һ�����ʱ����,�����Ҫ���⴦��ѵ�ǰ����+1��,ת��Ϊ<2012-01-02���в�ѯ
            if (matchValue instanceof Date) {
                DateTime dateTime = new DateTime(((Date) matchValue).getTime());
                if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0
                        && dateTime.getSecondOfMinute() == 0) {
                    return builder.lessThan(expression, dateTime.plusDays(1).toDate());
                }
            }
            predicate = builder.lessThan(expression, (Comparable) matchValue);
            break;
        case LE:
                // ���������⴦��һ�������������ڵĽ���ʱ���ѯ,���ѯ2012-01-01֮ǰ,һ����Ҫ��ʾ2010-01-01���켰��ǰ������,
                // �����ݿ�һ�����ʱ����,�����Ҫ���⴦��ѵ�ǰ����+1��,ת��Ϊ<2012-01-02���в�ѯ
            if (matchValue instanceof Date) {
                DateTime dateTime = new DateTime(((Date) matchValue).getTime());
                if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0
                        && dateTime.getSecondOfMinute() == 0) {
                    return builder.lessThan(expression, dateTime.plusDays(1).toDate());
                }
            }
            predicate = builder.lessThanOrEqualTo(expression, (Comparable) matchValue);
            break;
        case IN:
            if (matchValue.getClass().isArray()) {
                predicate = expression.in((Object[]) matchValue);
            } else if (matchValue instanceof Collection) {
                predicate = expression.in((Collection) matchValue);
            } else {
                predicate = builder.equal(expression, matchValue);
            }
            break;
        case ACLPREFIXS:
            List<Predicate> aclPredicates = Lists.newArrayList();
            Collection<String> aclCodePrefixs = (Collection<String>) matchValue;
            for (String aclCodePrefix : aclCodePrefixs) {
                if (StringUtils.isNotBlank(aclCodePrefix)) {
                    aclPredicates.add(builder.like(expression, aclCodePrefix + "%"));
                }

            }
            if (aclPredicates.size() == 0) {
                return null;
            }
            predicate = builder.or(aclPredicates.toArray(new Predicate[aclPredicates.size()]));
            break;
        default:
            break;
        }

        //�������Ӳ�ѯ
        if (filter.getSubQueryCollectionPropetyType() != null) {
            String owner = StringUtils.uncapitalize(entityClass.getSimpleName());
            subQueryFrom.join(owner);
            subquery.select(subQueryFrom.get(owner)).where(predicate);
            predicate = builder.in(root.get("id")).value(subquery);
        }

        Assert.notNull(predicate, "Undefined match type: " + filter.getMatchType());
        return predicate;
    }

    /**
     * �����������϶�����װJPA�淶������ѯ���϶��󣬻���Ĭ��ʵ�ֽ���������װ���
     * ������Ե��ô˷����ڷ��ص�List<Predicate>����׷������PropertyFilter���ױ���������exist���������
     * 
     * @param filters
     * @param root
     * @param query
     * @param builder
     * @return
     */
    private <X> List<Predicate> buildPredicatesFromFilters(final Collection<PropertyFilter> filters, Root<X> root,
            CriteriaQuery<?> query, CriteriaBuilder builder, Boolean having) {
        List<Predicate> predicates = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(filters)) {
            for (PropertyFilter filter : filters) {
                if (!filter.hasMultiProperties()) { // ֻ��һ��������Ҫ�Ƚϵ����.
                    Predicate predicate = buildPredicate(filter.getPropertyName(), filter, root, query, builder, having);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                } else {// �������������Ҫ�Ƚϵ����,����or����.
                    List<Predicate> orpredicates = Lists.newArrayList();
                    for (String param : filter.getPropertyNames()) {
                        Predicate predicate = buildPredicate(param, filter, root, query, builder, having);
                        if (predicate != null) {
                            orpredicates.add(predicate);
                        }
                    }
                    if (orpredicates.size() > 0) {
                        predicates.add(builder.or(orpredicates.toArray(new Predicate[orpredicates.size()])));
                    }
                }
            }
        }
        return predicates;
    }

    private <X extends Persistable> Specification<X> buildSpecification(final GroupPropertyFilter groupPropertyFilter) {
        return new Specification<X>() {
            @Override
            public Predicate toPredicate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (groupPropertyFilter != null) {
                    return buildPredicatesFromFilters(groupPropertyFilter, root, query, builder);
                } else {
                    return null;
                }
            }
        };
    }

    protected Predicate buildPredicatesFromFilters(GroupPropertyFilter groupPropertyFilter, Root root,
            CriteriaQuery<?> query, CriteriaBuilder builder) {
        return buildPredicatesFromFilters(groupPropertyFilter, root, query, builder, false);
    }

    protected Predicate buildPredicatesFromFilters(GroupPropertyFilter groupPropertyFilter, Root root,
            CriteriaQuery<?> query, CriteriaBuilder builder, Boolean having) {
        if (groupPropertyFilter == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Predicate> predicates = buildPredicatesFromFilters(groupPropertyFilter.getFilters(), root, query, builder,
                having);
        if (CollectionUtils.isNotEmpty(groupPropertyFilter.getGroups())) {
            for (GroupPropertyFilter group : groupPropertyFilter.getGroups()) {
                if (CollectionUtils.isEmpty(group.getFilters()) && CollectionUtils.isEmpty(group.getForceAndFilters())) {
                    continue;
                }
                Predicate subPredicate = buildPredicatesFromFilters(group, root, query, builder, having);
                if (subPredicate != null) {
                    predicates.add(subPredicate);
                }
            }
        }
        Predicate predicate = null;
        if (CollectionUtils.isNotEmpty(predicates)) {
            if (predicates.size() == 1) {
                predicate = predicates.get(0);
            } else {
                if (groupPropertyFilter.getGroupType().equals(GroupPropertyFilter.GROUP_OPERATION_OR)) {
                    predicate = builder.or(predicates.toArray(new Predicate[predicates.size()]));
                } else {
                    predicate = builder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            }
        }

        List<Predicate> appendAndPredicates = buildPredicatesFromFilters(groupPropertyFilter.getForceAndFilters(),
                root, query, builder, having);
        if (CollectionUtils.isNotEmpty(appendAndPredicates)) {
            if (predicate != null) {
                appendAndPredicates.add(predicate);
            }
            predicate = builder.and(appendAndPredicates.toArray(new Predicate[appendAndPredicates.size()]));
        }

        return predicate;
    }

    /**
     * �������׷�ӹ���������������ڷ�����һ����ڵ�ǰ��¼�û�ǿ��׷�ӹ�������
     * 
     * @param filters
     */
    protected List<Predicate> appendPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return null;
    }

    private Expression parseExpr(Root<?> root, CriteriaBuilder criteriaBuilder, String expr,
            Map<String, Expression<?>> parsedExprMap) {
        if (parsedExprMap == null) {
            parsedExprMap = Maps.newHashMap();
        }
        Expression<?> expression = null;
        if (expr.indexOf("(") > -1) {
            int left = 0;
            char[] chars = expr.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '(') {
                    left = i;
                }
            }
            String leftStr = expr.substring(0, left);
            String op = null;
            char[] leftStrs = leftStr.toCharArray();
            for (int i = leftStrs.length - 1; i > 0; i--) {
                if (leftStrs[i] == '(' || leftStrs[i] == ')' || leftStrs[i] == ',') {
                    op = leftStr.substring(i + 1);
                    break;
                }
            }
            if (op == null) {
                op = leftStr;
            }
            String rightStr = expr.substring(left + 1);
            String arg = StringUtils.substringBefore(rightStr, ")");
            String[] args = arg.split(",");
            //logger.debug("op={},arg={}", op, arg);
            if (op.equalsIgnoreCase("case")) {
                Case selectCase = criteriaBuilder.selectCase();

                Expression caseWhen = parsedExprMap.get(args[0]);

                String whenResultExpr = args[1];
                Object whenResult = parsedExprMap.get(whenResultExpr);
                if (whenResult == null) {
                    Case<Long> whenCase = selectCase.when(caseWhen, new BigDecimal(whenResultExpr));
                    selectCase = whenCase;
                } else {
                    Case<Expression<?>> whenCase = selectCase.when(caseWhen, whenResult);
                    selectCase = whenCase;
                }
                String otherwiseResultExpr = args[2];
                Object otherwiseResult = parsedExprMap.get(otherwiseResultExpr);
                if (otherwiseResult == null) {
                    expression = selectCase.otherwise(new BigDecimal(otherwiseResultExpr));
                } else {
                    expression = selectCase.otherwise((Expression<?>) otherwiseResult);
                }
            } else {
                Object[] subExpressions = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    subExpressions[i] = parsedExprMap.get(args[i]);
                    if (subExpressions[i] == null) {
                        String name = args[i];
                        try {
                            Path<?> item = null;
                            if (name.indexOf(".") > -1) {
                                String[] props = StringUtils.split(name, ".");
                                item = root.get(props[0]);
                                for (int j = 1; j < props.length; j++) {
                                    item = item.get(props[j]);
                                }
                            } else {
                                item = root.get(name);
                            }
                            subExpressions[i] = item;
                        } catch (Exception e) {
                            subExpressions[i] = new BigDecimal(name);
                        }
                    }
                }
                try {
                    //criteriaBuilder.quot();
                    expression = (Expression) MethodUtils.invokeMethod(criteriaBuilder, op, subExpressions);
                } catch (Exception e) {
                    logger.error("Error for aggregate  setting ", e);
                }
            }

            String exprPart = op + "(" + arg + ")";
            String exprPartConvert = exprPart.replace(op + "(", op + "_").replace(arg + ")", arg + "_")
                    .replace(",", "_");
            expr = expr.replace(exprPart, exprPartConvert);
            parsedExprMap.put(exprPartConvert, expression);

            if (expr.indexOf("(") > -1) {
                expression = parseExpr(root, criteriaBuilder, expr, parsedExprMap);
            }
        } else {
            String name = expr;
            Path<?> item = null;
            if (name.indexOf(".") > -1) {
                String[] props = StringUtils.split(name, ".");
                item = root.get(props[0]);
                for (int j = 1; j < props.length; j++) {
                    item = item.get(props[j]);
                }
            } else {
                item = root.get(name);
            }
            expression = item;
        }
        return expression;
    }

    private String fixCleanAlias(String name) {
        return StringUtils.remove(StringUtils.remove(
                StringUtils.remove(StringUtils.remove(StringUtils.remove(name, "("), ")"), "."), ","), "-");
    }

    private Expression<?> buildExpression(Root<?> root, CriteriaBuilder criteriaBuilder, String name, String alias) {
        Expression<?> expr = parseExpr(root, criteriaBuilder, name, null);
        if (alias != null) {
            expr.alias(alias);
        }
        return expr;
    }

    private Expression<?>[] buildExpressions(Root<?> root, CriteriaBuilder criteriaBuilder,
            List<GroupAggregateProperty> groupAggregateProperties) {
        Expression<?>[] parsed = new Expression<?>[groupAggregateProperties.size()];
        int i = 0;
        for (GroupAggregateProperty groupAggregateProperty : groupAggregateProperties) {
            parsed[i++] = buildExpression(root, criteriaBuilder, groupAggregateProperty.getName(),
                    groupAggregateProperty.getAlias());
        }
        return parsed;
    }

    private Selection<?>[] mergeSelections(Root<?> root, Selection<?>[] path1, Selection<?>... path2) {
        Selection<?>[] parsed = new Selection<?>[path1.length + path2.length];
        int i = 0;
        for (Selection<?> path : path1) {
            parsed[i++] = path;
        }
        for (Selection<?> path : path2) {
            parsed[i++] = path;
        }
        return parsed;
    }

    /**
     * ��ѯ������ʷ��¼�汾����
     * 
     * @param id
     *            ʵ������
     * @param property
     *            ��������
     * @param changed
     *            ���˷�ʽ�����ޱ��
     * @return
     */
    @Transactional(readOnly = true)
    public List<EntityRevision> findEntityRevisions(final Object id, String property, Boolean changed) {
        List<EntityRevision> entityRevisions = Lists.newArrayList();
        AuditQuery auditQuery = AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(entityClass, false, true);
        auditQuery.add(AuditEntity.id().eq(id)).addOrder(AuditEntity.revisionNumber().desc());
        if (StringUtils.isNotBlank(property) && changed != null) {
            if (changed) {
                auditQuery.add(AuditEntity.property(property).hasChanged());
            } else {
                auditQuery.add(AuditEntity.property(property).hasNotChanged());
            }
        }
        List list = auditQuery.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Object obj : list) {
                Object[] itemArray = (Object[]) obj;
                EntityRevision entityRevision = new EntityRevision();
                entityRevision.setEntity(itemArray[0]);
                entityRevision.setRevisionEntity((ExtDefaultRevisionEntity) itemArray[1]);
                entityRevision.setRevisionType((RevisionType) itemArray[2]);
                entityRevisions.add(entityRevision);
            }
        }
        return entityRevisions;
    }

    /**
     * ��ѯ������ʷ��¼�汾����
     * 
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<EntityRevision> findEntityRevisions(final ID id, Number... revs) {
        List<EntityRevision> entityRevisions = Lists.newArrayList();
        AuditQuery auditQuery = AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(entityClass, false, true);
        auditQuery.add(AuditEntity.id().eq(id)).add(AuditEntity.revisionNumber().in(revs));
        List list = auditQuery.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Object obj : list) {
                Object[] itemArray = (Object[]) obj;
                EntityRevision entityRevision = new EntityRevision();
                entityRevision.setEntity(itemArray[0]);
                entityRevision.setRevisionEntity((ExtDefaultRevisionEntity) itemArray[1]);
                entityRevision.setRevisionType((RevisionType) itemArray[2]);
                entityRevisions.add(entityRevision);
            }
        }
        return entityRevisions;
    }

    /**
     * ��������õĹ������������ϵ������������
     * 
     * @param id
     *            ��ǰ������������������User��������
     * @param r2EntityIds
     *            ����������������ϣ����û�������ɫ��Role���󼯺ϵ�����
     * @param r2PropertyName
     *            �������й������϶������Ե����ƣ���User�����ж����userR2Roles������
     * @param r2EntityPropertyName
     *            ������������R2�����������е��������ƣ���UserR2Role�ж����role������
     * @param op
     *            �����������ͣ���add��del�ȣ� @see #R2OperationEnum
     */
    protected void updateRelatedR2s(ID id, Collection<? extends Serializable> r2EntityIds, String r2PropertyName,
            String r2EntityPropertyName, R2OperationEnum op) {
        try {
            T entity = findOne(id);
            List oldR2s = (List) FieldUtils.readDeclaredField(entity, r2PropertyName, true);

            Field r2field = FieldUtils.getField(entityClass, r2PropertyName, true);
            Class r2Class = (Class) (((ParameterizedType) r2field.getGenericType()).getActualTypeArguments()[0]);
            Field entityField = null;
            Field[] fields = r2Class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(entityClass)) {
                    entityField = field;
                    break;
                }
            }

            Field r2EntityField = FieldUtils.getField(r2Class, r2EntityPropertyName, true);
            Class r2EntityClass = r2EntityField.getType();

            if (R2OperationEnum.update.equals(op)) {
                if (CollectionUtils.isEmpty(r2EntityIds) && !CollectionUtils.isEmpty(oldR2s)) {
                    oldR2s.clear();
                }
            }

            if (R2OperationEnum.update.equals(op) || R2OperationEnum.add.equals(op)) {
                // ˫ѭ��������Ҫ������������Ŀ
                for (Serializable r2EntityId : r2EntityIds) {
                    Object r2Entity = entityManager.find(r2EntityClass, r2EntityId);
                    boolean tobeAdd = true;
                    for (Object r2 : oldR2s) {
                        if (FieldUtils.readDeclaredField(r2, r2EntityPropertyName, true).equals(r2Entity)) {
                            tobeAdd = false;
                            break;
                        }
                    }
                    if (tobeAdd) {
                        Object newR2 = r2Class.newInstance();
                        FieldUtils.writeDeclaredField(newR2, r2EntityField.getName(), r2Entity, true);
                        FieldUtils.writeDeclaredField(newR2, entityField.getName(), entity, true);
                        oldR2s.add(newR2);
                    }
                }
            }

            if (R2OperationEnum.update.equals(op)) {
                // ˫ѭ��������Ҫɾ����������Ŀ
                List tobeDleteList = Lists.newArrayList();
                for (Object r2 : oldR2s) {
                    boolean tobeDlete = true;
                    for (Serializable r2EntityId : r2EntityIds) {
                        Object r2Entity = entityManager.find(r2EntityClass, r2EntityId);
                        if (FieldUtils.readDeclaredField(r2, r2EntityPropertyName, true).equals(r2Entity)) {
                            tobeDlete = false;
                            break;
                        }
                    }
                    if (tobeDlete) {
                        tobeDleteList.add(r2);
                    }
                }
                oldR2s.removeAll(tobeDleteList);
            }

            if (R2OperationEnum.delete.equals(op)) {
                // ˫ѭ��������Ҫɾ����������Ŀ
                List tobeDleteList = Lists.newArrayList();
                for (Object r2 : oldR2s) {
                    boolean tobeDlete = false;
                    for (Serializable r2EntityId : r2EntityIds) {
                        Object r2Entity = entityManager.find(r2EntityClass, r2EntityId);
                        if (FieldUtils.readDeclaredField(r2, r2EntityPropertyName, true).equals(r2Entity)) {
                            tobeDlete = true;
                            break;
                        }
                    }
                    if (tobeDlete) {
                        tobeDleteList.add(r2);
                    }
                }
                oldR2s.removeAll(tobeDleteList);
            }

        } catch (SecurityException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * ��������õĹ������������ϵ������������
     * 
     * @param id
     *            ��ǰ������������������User��������
     * @param r2EntityIds
     *            ����������������ϣ����û�������ɫ��Role���󼯺ϵ�����
     * @param r2PropertyName
     *            �������й������϶������Ե����ƣ���User�����ж����userR2Roles������
     * @param r2EntityPropertyName
     *            ������������R2�����������е��������ƣ���UserR2Role�ж����role������
     */
    protected void updateRelatedR2s(ID id, Serializable[] r2EntityIds, String r2PropertyName,
            String r2EntityPropertyName) {
        try {
            T entity = findOne(id);
            List oldR2s = (List) MethodUtils.invokeExactMethod(entity, "get" + StringUtils.capitalize(r2PropertyName),
                    null);

            if ((r2EntityIds == null || r2EntityIds.length == 0) && !CollectionUtils.isEmpty(oldR2s)) {
                oldR2s.clear();
            } else {
                Field r2field = FieldUtils.getField(entityClass, r2PropertyName, true);
                Class r2Class = (Class) (((ParameterizedType) r2field.getGenericType()).getActualTypeArguments()[0]);
                Field entityField = null;
                Field[] fields = r2Class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType().equals(entityClass)) {
                        entityField = field;
                        break;
                    }
                }

                Field r2EntityField = FieldUtils.getField(r2Class, r2EntityPropertyName, true);
                Class r2EntityClass = r2EntityField.getType();

                // ˫ѭ��������Ҫɾ����������Ŀ
                List tobeDleteList = Lists.newArrayList();
                for (Object r2 : oldR2s) {
                    boolean tobeDlete = true;
                    for (Serializable r2EntityId : r2EntityIds) {
                        Object r2Entity = entityManager.find(r2EntityClass, r2EntityId);
                        if (FieldUtils.readDeclaredField(r2, r2EntityPropertyName, true).equals(r2Entity)) {
                            tobeDlete = false;
                            break;
                        }
                    }
                    if (tobeDlete) {
                        tobeDleteList.add(r2);
                    }
                }
                oldR2s.removeAll(tobeDleteList);

                // ˫ѭ��������Ҫ������������Ŀ
                for (Serializable r2EntityId : r2EntityIds) {
                    Object r2Entity = entityManager.find(r2EntityClass, r2EntityId);
                    boolean tobeAdd = true;
                    for (Object r2 : oldR2s) {
                        if (FieldUtils.readDeclaredField(r2, r2EntityPropertyName, true).equals(r2Entity)) {
                            tobeAdd = false;
                            break;
                        }
                    }
                    if (tobeAdd) {
                        Object newR2 = r2Class.newInstance();
                        FieldUtils.writeDeclaredField(newR2, r2EntityField.getName(), r2Entity, true);
                        FieldUtils.writeDeclaredField(newR2, entityField.getName(), entity, true);
                        oldR2s.add(newR2);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(readOnly = true)
    public Object findEntity(Class entityClass, Serializable id) {
        return entityManager.find(entityClass, id);
    }

    public void detachEntity(Object entity) {
        entityManager.detach(entity);
    }

    /**
     * ����Native SQL����Map�ṹ��������
     */
    @SuppressWarnings("rawtypes")
    protected List<Map<String, Object>> queryForMapDatasByNativeSQL(String sql) {
        //TODO ��ֹSQL�ַ����˴���
        Query query = entityManager.createNativeQuery(sql);
        //Hibernate�ض��﷨
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List list = query.getResultList();
        entityManager.close();
        return list;
    }
}
