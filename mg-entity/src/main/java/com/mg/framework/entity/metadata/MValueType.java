package com.mg.framework.entity.metadata;

/**
 * 表达式 ，值类型
 * 即：在关系中“元数据 = value” value的值类型
 */
public enum MValueType {
    /**
     * 常量，用的最多
     */
    CONST,
    /**
     * 值为另外一个元数据
     */
    M_PROPERTY,
    /**
     * 调用者属性值
     */
    CALLER_PROP,

    /**
     * 当前日期。
     */
    CURRENT_DATE,
    /**
     * Groovy 脚本。
     */
    GROOVY
}
