package com.mg.report.service;

import com.mg.report.entity.ReportChartEntity;
import com.mg.report.vo.ReportChartData;

import java.util.List;

/**
 * Created by liukefu on 2016/4/18.
 */
public interface ReportChartService {

    ReportChartEntity findReport(String id);

    void saveReport(ReportChartEntity report);

    ReportChartData queryReportData(String id);

    List<ReportChartEntity> findChartByReport(String id);

    void delete(String id);
}
