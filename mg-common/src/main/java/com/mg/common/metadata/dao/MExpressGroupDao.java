package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MExpressGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by liukefu on 2015/10/3.
 */
public interface MExpressGroupDao  extends
        JpaRepository<MExpressGroupEntity, String>,
        QueryDslPredicateExecutor<MExpressGroupEntity> {

}
