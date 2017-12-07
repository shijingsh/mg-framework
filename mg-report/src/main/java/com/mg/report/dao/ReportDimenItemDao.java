package com.mg.report.dao;

import com.mg.report.entity.ReportDimenItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by liukefu on 2015/10/24.
 */
public interface ReportDimenItemDao extends JpaRepository<ReportDimenItemEntity,String>,
        QueryDslPredicateExecutor<ReportDimenItemEntity> {
}
