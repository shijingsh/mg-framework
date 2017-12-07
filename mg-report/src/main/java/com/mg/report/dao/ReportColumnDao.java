package com.mg.report.dao;

import com.mg.report.entity.ReportColumnEntity;
import com.mg.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 *  报表显示列
 */
public interface ReportColumnDao extends JpaRepository<ReportColumnEntity,String>,
        QueryDslPredicateExecutor<ReportColumnEntity> {

        List<ReportColumnEntity> findByBelongReport(ReportEntity reportEntity);
}
