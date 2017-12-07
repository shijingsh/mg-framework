package com.mg.report.entity;

import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.*;

/**
 * 图表分类
 * Created by liukefu on 2016/4/18.
 */
@Entity
@Table(name="report_chart_category")
public class ChartCategoryEntity extends BaseEntity {
    /**
     * 图表分类名称
     * 柱状图、线性图
     */
    private String name;

    /**
     * echarts的图表分类
     * bar、pie、line
     */
    private String type;
    /**
     * 图表默认配置
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String options;
    /**
     * 默认数据项配置
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String series;

    /**
     * 依赖的图表
     * bar;pie;line
     */
    private String required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}
