package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 元数据对象接口表
 * 为其他模块提供业务数据
 * Created by liukefu on 2015/12/18.
 */
@Entity
@Table(name="sys_meta_interface")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MInterfaceEntity extends BaseEntity {
    /**
     * 接口数据所属模块名称
     * 默认：hr
     */
    private String moduleName  = "hr";
    /**
     * 接口数据名称
     * 比如：例考成绩系数 考勤管理扣分等
     */
    private String name;
    /**
     * 元数据对象名称
     * 从哪个表取数据
     */
    private String objectName;

    /**
     * 元数据对象名称属性名称
     * 取哪个字段的数据：比如 姓名
     * 结构化字段则形式如：获得证书.获得证书名称
     */
    private String propertyName;
    /**
     * 接口实现类名称
     */
    private String interfaceImplName = "MInterfaceServiceImpl";

    /**
     * 取数据的限制条件 过滤值
     * 表达式右值 常量
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String filterValue;
    /**
     * 取数据的限制条件的关系 过滤值关系
     * 表达式关系
     * 见：MRelationEnum
     */
    private Integer filterRelation;

    /**
     * 接口返回类型
     */
    @Enumerated(EnumType.STRING)
    private MInterfaceReturnTypeEnum returnTypeEnum;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    public MInterfaceReturnTypeEnum getReturnTypeEnum() {
        return returnTypeEnum;
    }

    public void setReturnTypeEnum(MInterfaceReturnTypeEnum returnTypeEnum) {
        this.returnTypeEnum = returnTypeEnum;
    }

    public String getInterfaceImplName() {
        return interfaceImplName;
    }

    public void setInterfaceImplName(String interfaceImplName) {
        this.interfaceImplName = interfaceImplName;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public Integer getFilterRelation() {
        return filterRelation;
    }

    public void setFilterRelation(Integer filterRelation) {
        this.filterRelation = filterRelation;
    }
}
