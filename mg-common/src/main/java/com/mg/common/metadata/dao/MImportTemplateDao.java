package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MImportTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by liukefu on 2015/10/21.
 */
public interface MImportTemplateDao extends
        JpaRepository<MImportTemplateEntity, String>,
        QueryDslPredicateExecutor<MImportTemplateEntity> {

}
