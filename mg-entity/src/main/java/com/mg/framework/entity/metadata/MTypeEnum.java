package com.mg.framework.entity.metadata;

/**
 * 元数据类型
 *
 * @author liukefu
 */
public enum MTypeEnum {
    /**
     * 普通字段
     */
    normal,
    /**
     * 头像
     */
    headPortrait,
    /**
     * 图片
     */
    image,
    /**
     * 文件附件
     */
    file,
    /**
     * 关联对象
     * 存在于：MObjectEntity
     * 比如：员工、岗位 ，这种关系，创建主对象时用于选择器，直接与之建立关联关系
     */
    mObject,
    /**
     * 枚举对象
     * 枚举存在于：MEnumEntity
     */
    mEnum,
    /**
     * 结构化字段
     * 逻辑虚拟的字段，不在表结构中，暂时不参与查询
     * 只用于自定义表单显示结构化数据
     */
    subType
}
