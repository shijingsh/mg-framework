package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MInterfaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
public interface MInterfaceDao extends
        JpaRepository<MInterfaceEntity, String>,
        QueryDslPredicateExecutor<MInterfaceEntity> {

       List<MInterfaceEntity> findByName(String name);
}
