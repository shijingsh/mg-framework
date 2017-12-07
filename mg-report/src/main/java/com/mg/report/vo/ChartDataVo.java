package com.mg.report.vo;

/**
 * 图表数据
 * 对应 series  --> data
 * Created by liukefu on 2016/4/20.
 */
public class ChartDataVo {

    private String name;
    private Object value;

    public ChartDataVo() {
    }

    public ChartDataVo(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
