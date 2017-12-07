package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */

public interface MirrorPropertyDao extends
        JpaRepository<MirrorPropertyEntity, String>,
        QueryDslPredicateExecutor<MirrorPropertyEntity> {

        public List<MirrorPropertyEntity> findByRootMObjectAndPropertyPath(MObjectEntity rootMObject, String propertyPath);

}