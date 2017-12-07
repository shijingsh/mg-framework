package com.mg.report.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.*;

/**
 * 图表
 * Created by liukefu on 2016/4/18.
 */
@Entity
@Table(name="report_chart")
public class ReportChartEntity extends BaseEntity {

    /**
     * 图表名称
     */
    private String name;
    /**
     * 图表风格
     */
    private String theme;
    /**
     * 设置图表高度
     */
    private Integer height;
    /**
     * 所属报表
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_report_id")
    private ReportEntity belongReport;

    /**
     * 图表分类
     */
    @ManyToOne
    @JoinColumn(name = "chart_category")
    private ChartCategoryEntity chartCategory;

    /**
     * 图表的数据模型
     */
    @Enumerated(EnumType.STRING)
    private ChartDataModelEnum dataModelEnum = ChartDataModelEnum.normal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ReportEntity getBelongReport() {
        return belongReport;
    }

    public void setBelongReport(ReportEntity belongReport) {
        this.belongReport = belongReport;
    }

    public ChartCategoryEntity getChartCategory() {
        return chartCategory;
    }

    public void setChartCategory(ChartCategoryEntity chartCategory) {
        this.chartCategory = chartCategory;
    }

    public ChartDataModelEnum getDataModelEnum() {
        return dataModelEnum;
    }

    public void setDataModelEnum(ChartDataModelEnum dataModelEnum) {
        this.dataModelEnum = dataModelEnum;
    }
}
