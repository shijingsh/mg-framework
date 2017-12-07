package com.mg.report.vo;

import java.math.BigDecimal;

/**
 * 维度统计数据vo
 * Created by liukefu on 2016/3/29.
 */
public class DimenDataVO implements java.io.Serializable{
    /**
     * 最大值
     */
    private BigDecimal maxValue = new BigDecimal(Integer.MIN_VALUE);
    /**
     * 最小值
     */
    private BigDecimal minValue = new BigDecimal(Integer.MIN_VALUE);
    /**
     * 合计值
     */
    private BigDecimal total = new BigDecimal(0);
    /**
     * 平均值
     */
    private BigDecimal avg = new BigDecimal(0);
    /**
     * 个数
     */
    private Integer count = 0;

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
