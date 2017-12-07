package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by liukefu on 2015/9/11.
 */

public interface MTemplateDao extends
        JpaRepository<MTemplateEntity, String>,
        QueryDslPredicateExecutor<MTemplateEntity> {

        @Query("select max(u.sort) from MTemplateEntity u where u.belongMObject = ?1 and u.templateType=?2")
        public Integer maxSort(MObjectEntity belongMObject, MTemplateTypeEnum templateType);
}
