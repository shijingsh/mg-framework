package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MFavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by liukefu on 2015/10/2.
 */
public interface MFavoritesDao extends
        JpaRepository<MFavoritesEntity, String>,
        QueryDslPredicateExecutor<MFavoritesEntity> {
}
