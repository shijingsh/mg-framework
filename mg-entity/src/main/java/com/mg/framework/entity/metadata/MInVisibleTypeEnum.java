package com.mg.framework.entity.metadata;

/**
 * 元数据隐藏类型
 *
 * @author liukefu
 */
public enum MInVisibleTypeEnum {
    /**
     * 不隐藏
     */
    invisibleNone,
    /**
     * 全部隐藏
     */
    invisibleAll,
    /**
     * 数据录入时隐藏
     */
    invisibleDataEntry,

    /**
     * 数据列表时隐藏
     */
    invisibleDataList,
    /**
     * 数据录入时和列表时隐藏
     */
    invisibleDataEntryList,
    /**
     * 数据查看时隐藏
     */
    invisibleDataView,
}
