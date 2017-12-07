package com.mg.report.entity;

/**
 * 图表的
 * Created by liukefu on 2016/4/20.
 */
public enum ChartDataModelEnum {
    /**
     * series : [
     * {
     * name:'最高气温',
     * type:'line',
     * data:[11, 11, 15, 13, 12, 13, 10]
     * },
     * {
     * name:'最低气温',
     * type:'line',
     * data:[1, -2, 2, 5, 3, 2, 0]
     * }
     * ]
     */
    normal,
    /**
     * series : [
     * {
     * name:'访问来源',
     * type:'pie',
     * radius : '55%',
     * center: ['50%', '60%'],
     * data:[
     * {value:335, name:'直接访问'},
     * {value:310, name:'邮件营销'},
     * {value:234, name:'联盟广告'},
     * {value:135, name:'视频广告'},
     * {value:1548, name:'搜索引擎'}
     * ]
     * }
     * ]
     */
    simple
}
