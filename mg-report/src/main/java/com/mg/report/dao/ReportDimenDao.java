package com.mg.report.dao;

import com.mg.report.entity.ReportDimenEntity;
import com.mg.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2015/10/24.
 */
public interface ReportDimenDao extends JpaRepository<ReportDimenEntity,String>,
        QueryDslPredicateExecutor<ReportDimenEntity> {

        List<ReportDimenEntity> findByBelongReport(ReportEntity reportEntity);
}
