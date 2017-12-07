package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MObjectScriptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */

public interface MObjectScriptDao extends
        JpaRepository<MObjectScriptEntity, String>,
        QueryDslPredicateExecutor<MObjectScriptEntity> {
    public List<MObjectScriptEntity> findByIsEnable(boolean isEnable);

    public List<MObjectScriptEntity> findByBelongMObjectAndIsEnable(MObjectEntity belongMObject, Boolean isEnable);

    public List<MObjectScriptEntity> findByBelongMObjectAndExecOnInsertAndIsEnable(MObjectEntity belongMObject, Boolean execOnInsert, Boolean isEnable);

    public List<MObjectScriptEntity> findByBelongMObjectAndExecOnUpdateAndIsEnable(MObjectEntity belongMObject, Boolean execOnUpdate, Boolean isEnable);
}
