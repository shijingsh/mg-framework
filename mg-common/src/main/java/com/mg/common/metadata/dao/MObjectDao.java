package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */

public interface MObjectDao extends
        JpaRepository<MObjectEntity, String>,
        QueryDslPredicateExecutor<MObjectEntity> {
    public List<MObjectEntity> findByIsEnable(boolean isEnable);

    public List<MObjectEntity> findByNameAndIsEnable(String name, boolean isEnable);

    public List<MObjectEntity> findByTableNameAndIsEnable(String tableName, boolean isEnable);

    public List<MObjectEntity> findByModuleNameAndIsEnable(String moduleName, boolean isEnable);

    public List<MObjectEntity> findByModuleNameAndIsManageAndIsEnable(String moduleName, boolean isManage, boolean isEnable);
}
