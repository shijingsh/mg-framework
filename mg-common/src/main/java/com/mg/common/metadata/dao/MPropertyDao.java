package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */

public interface MPropertyDao extends
        JpaRepository<MPropertyEntity, String>,
        QueryDslPredicateExecutor<MPropertyEntity> {

    public List<MPropertyEntity> findByIsEnable(Boolean isEnable);

    public List<MPropertyEntity> findByBelongMObjectAndIsEnable(MObjectEntity belongMObject, Boolean isEnable);

    public List<MPropertyEntity> findByBelongMObjectAndFieldName(MObjectEntity belongMObject, String fieldName);

    public List<MPropertyEntity> findByBelongMObjectAndFieldNameAndIsEnable(MObjectEntity belongMObject, String fieldName, Boolean isEnable);

    @Query("select max(u.sort) from MPropertyEntity u where u.belongMObject = ?1")
    public Integer maxSort(MObjectEntity belongMObject);
}