package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MSqlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
public interface MSqlDao extends
        JpaRepository<MSqlEntity, String>,
        QueryDslPredicateExecutor<MSqlEntity> {

    List<MSqlEntity> findByNameOrderBySortAsc(String name);

    List<MSqlEntity> findByCategoryNameOrderBySortAsc(String name);
}
