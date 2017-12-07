package com.mg.report.dao;

import com.mg.report.entity.ReportChartEntity;
import com.mg.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by liukefu on 2016/4/18.
 */
public interface ReportChartDao extends JpaRepository<ReportChartEntity, String>,
        QueryDslPredicateExecutor<ReportChartEntity> {

        List<ReportChartEntity> findChartByBelongReport(ReportEntity belongReport);
}
