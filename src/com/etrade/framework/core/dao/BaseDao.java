package com.etrade.framework.core.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>,
        JpaSpecificationExecutor<T> {

}
