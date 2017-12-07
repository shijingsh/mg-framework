package com.mg.report.entity;

/**
 * 报表统计方法枚举
 * @author liukefu
 */
public enum StatisticalMethodEnum {
    /**
     * 显示
     * 只是显示某个字段的值，不做统计，用于基础信息报表
     */
    FOR_SHOW,
    /**
     * 计数
     */
    COUNT,
    /**
     * 求和
     */
    SUM,
    /**
     * 平均值
     */
    AVG,
    /**
     * 最大值
     */
    MAX,
    /**
     * 最小值
     */
    MIN
}
