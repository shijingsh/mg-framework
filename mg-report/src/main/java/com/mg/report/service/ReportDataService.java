package com.mg.report.service;

import com.mg.report.vo.ReportVo;

/**
 * 获取报表数据接口
 * Created by liukefu on 2015/11/4.
 */
public interface ReportDataService {

    /**
     * 返回报表数据
     * @param reportId
     * @return
     */
    public ReportVo queryReportData(String reportId);
}
