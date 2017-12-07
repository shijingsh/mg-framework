package com.mg.framework.entity.metadata;

/**
 * Created by liukefu on 2015/12/18.
 */
public enum MInterfaceReturnTypeEnum {
    /**
     * 字段的值直接返回
     * 此项为默认值，有多条匹配的时候，以分号“;”分隔
     */
    normal,
    /**
     * 返回一条
     */
    single,
    /**
     * 返回匹配的记录数
     */
    count,
    /**
     * 返回匹配的记录数之和
     * 只能数字的时候使用
     */
    sum,
    /**
     * 执行脚本
     */
    groovy
}
