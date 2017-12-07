package com.mg.report.vo;

import com.mg.report.entity.ReportChartEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据vo
 * Created by liukefu on 2016/4/18.
 */
public class ReportChartData {

    ReportChartEntity reportChart;
    /**
     *  图例
     *  列维度
     */
    private List<String> legendData = new ArrayList<>();
    /**
     *  行维度
     */
    private List<String> rowDimen  = new ArrayList<>();
    /**
     * 数据内容数组
     */
    private List<?> seriesData = new ArrayList<>();

    public ReportChartEntity getReportChart() {
        return reportChart;
    }

    public void setReportChart(ReportChartEntity reportChart) {
        this.reportChart = reportChart;
    }

    public List<String> getLegendData() {
        return legendData;
    }

    public void setLegendData(List<String> legendData) {
        this.legendData = legendData;
    }

    public List<String> getRowDimen() {
        return rowDimen;
    }

    public void setRowDimen(List<String> rowDimen) {
        this.rowDimen = rowDimen;
    }

    public List<?> getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(List<?> seriesData) {
        this.seriesData = seriesData;
    }
}
