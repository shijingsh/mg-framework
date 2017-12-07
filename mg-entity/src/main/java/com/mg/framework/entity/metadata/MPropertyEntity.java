package com.mg.framework.entity.metadata;

import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 元数据
 * 元数据对象相当于一个张实体表
 * 而元数据相当于表中的一个字段
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_property")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MPropertyEntity extends BaseEntity {
    /**
     * 元数据名称
     * 也是中文名称 比如：姓名
     */
    private String name;
    /**
     * 第二名称
     * 比如：组织名称，也可能叫网点名称
     * 用于查询元数据时findByName使用，名称等于secondName时，也视为匹配
     */
    private String secondName;

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 字段名称
     * 比如：emp_name
     */
    private String fieldName;
    /**
     * 字段默认值
     * 比如：刘某某
     */
    private String fieldDefaultValue;

    /**
     * 字段类型
     * 比如：varchar
     */
    @Enumerated(EnumType.STRING)
    private MFieldTypeEnum fieldType;
    /**
     * 字段长度
     * 比如：255
     */
    private Integer fieldLength = 255;
    /**
     * 字段精度
     */
    private Integer fieldPrecision = 0;
    /**
     * 属性是否为主键
     */
    private Boolean isPrimaryKey = false;
    /**
     * 是否是可空
     */
    private Boolean isNullable = true;
    /**
     * 是否是激活的.
     */
    private Boolean isEnable = true;

    /**
     * 是否默认参与检索.
     * 默认不参与
     * 参与将默认显示在条件组里
     */
    private Boolean isSearchCondition = false;

    /**
     * 隐藏类型
     * 默认不隐藏
     */
    @Enumerated(EnumType.STRING)
    private MInVisibleTypeEnum inVisibleType = MInVisibleTypeEnum.invisibleNone;
    /**
     * 是否是只读的.
     */
    private Boolean isReadOnly = false;
    /**
     * 所属元数据对象
     * 元数据所在表
     */
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    protected MObjectEntity belongMObject;
    /**
     * 所属元数据类型
     * 包括：普通类型、对象类型、枚举类型、结构化数据
     */
    @Enumerated(EnumType.STRING)
    private MTypeEnum typeEnum = MTypeEnum.normal;

    /**
     * 元数据是一个元数据对象类型时所对应的对象
     * 不是对象类型时，值为空
     */
    @ManyToOne
    @JoinColumn(name = "mobject_id")
    protected MObjectEntity metaObject;

    /**
     * 元数据是一个元数据对象类型时，与该对象的外键字段
     * 关联对象：  外键为对象的主键
     * 结构化对象：外键为特定字段，如“所属员工”
     */
    @ManyToOne
    @JoinColumn(name = "mapped_by_property")
    private MPropertyEntity mappedByProperty;
    /**
     * 元数据是一个枚举类型，对应的枚举名称
     * 枚举存在于：MEnumEntity
     * 不是枚举类型，则值为空
     */
    protected  String enumName;

    /**
     * 字段生成脚本
     * 比如：编号自动生成
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String formula;

    /**
     * 排序
     */
    private Integer sort = 0;

    public MPropertyEntity() {
    }

    public MPropertyEntity(String name, String fieldName, MFieldTypeEnum fieldType, Integer fieldLength) {
        this.name = name;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldLength = fieldLength;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDefaultValue() {
        return fieldDefaultValue;
    }

    public void setFieldDefaultValue(String fieldDefaultValue) {
        this.fieldDefaultValue = fieldDefaultValue;
    }

    public MFieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(MFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public Integer getFieldPrecision() {
        return fieldPrecision;
    }

    public void setFieldPrecision(Integer fieldPrecision) {
        this.fieldPrecision = fieldPrecision;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(Boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public Boolean getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public MTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(MTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public MObjectEntity getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(MObjectEntity metaObject) {
        this.metaObject = metaObject;
    }

    public MPropertyEntity getMappedByProperty() {
        return mappedByProperty;
    }

    public void setMappedByProperty(MPropertyEntity mappedByProperty) {
        this.mappedByProperty = mappedByProperty;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public Boolean getIsSearchCondition() {
        return isSearchCondition;
    }

    public void setIsSearchCondition(Boolean isSearchCondition) {
        this.isSearchCondition = isSearchCondition;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(Boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public MInVisibleTypeEnum getInVisibleType() {
        return inVisibleType;
    }

    public void setInVisibleType(MInVisibleTypeEnum inVisibleType) {
        this.inVisibleType = inVisibleType;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
