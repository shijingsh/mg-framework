package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * 元数据
 * 元数据对象相当于一个张实体表
 * 而元数据相当于表中的一个字段
 * 和 MPropertyEntity 的区别在于：
 *          MPropertyEntity，用来生成具体MirrorPropertyEntity
 *          MirrorPropertyEntity 只是元数据的一个镜像
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_mirror_property")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MirrorPropertyEntity extends BaseEntity {

    /**
     * 所属的根元数据对象
     */
    @ManyToOne
    @JoinColumn(name = "root_mobject_id")
    @JSONField(serialize = false, deserialize = false)
    protected MObjectEntity rootMObject;
    /**
     * 直属元数据对象
     * 元数据所在表
     */
    @ManyToOne
    @JoinColumn(name = "belong_mobject_id")
    @JSONField(serialize = false, deserialize = false)
    protected MObjectEntity belongMObject;

    /**
     * 上级的元数据
     */
    @ManyToOne
    @JoinColumn(name = "parent_property_id")
    @JSONField(serialize = false, deserialize = false)
    protected MirrorPropertyEntity parentProperty;
    /**
     * 对应的元数据
     */
    @ManyToOne
    @JoinColumn(name = "m_property_id")
    @JSONField(serialize = false, deserialize = false)
    protected MPropertyEntity metaProperty;
    /**
     * 在对象树中的深度
     * 如：主对象元数据deep为1
     */
    private Integer deep;
    /**
     * 属性在对对象树中的路径
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String propertyPath;

    //---------冗余数据----------------------------------------
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
     * 是否可空
     */
    private Boolean isNullable = true;
    /**
     * 是否是检索条件
     */
    private Boolean isSearchCondition = false;

    /**
     * 是否是只读的.
     */
    private Boolean isReadOnly = false;

    /**
     * 隐藏类型
     * 默认不隐藏
     */
    @Enumerated(EnumType.STRING)
    private MInVisibleTypeEnum inVisibleType = MInVisibleTypeEnum.invisibleNone;
    /**
     * 显示的控件类型
     */
    @Enumerated(EnumType.STRING)
    private MControllerTypeEnum controllerType = MControllerTypeEnum.text;
    /**
     * 枚举名称
     */
    protected  String enumName;
    /**
     * 字段名称
     * 比如：emp_name
     */
    private String fieldName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 属性的对象ID
     * 当元数据是对象类型时候，记录对象的ID
     */
    private String propertyObjectId ;

    /**
     * 字段生成脚本
     * 比如：编号自动生成
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String formula;
    /**
     * 字段值
     * 比如：刘备
     * 用于接收页面上的传值
     */
    @Transient
    private Object fieldValue;
    /**
     * 上级元数据
     */
    @Transient
    private String parentPropertyId ;
    /**
     * 所属元数据对象
     */
    @Transient
    private String belongObjectId;

    /**
     * 根元数据对象
     */
    @Transient
    private String rootObjectId ;

    public MirrorPropertyEntity getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(MirrorPropertyEntity parentProperty) {
        this.parentProperty = parentProperty;
    }

    public MObjectEntity getRootMObject() {
        return rootMObject;
    }

    public void setRootMObject(MObjectEntity rootMObject) {
        this.rootMObject = rootMObject;
    }

    public MObjectEntity getBelongMObject() {
        return belongMObject;
    }

    public void setBelongMObject(MObjectEntity belongMObject) {
        this.belongMObject = belongMObject;
    }

    public MPropertyEntity getMetaProperty() {
        return metaProperty;
    }

    public void setMetaProperty(MPropertyEntity metaProperty) {
        this.metaProperty = metaProperty;
    }

    public Integer getDeep() {
        return deep;
    }

    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public Boolean getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public MControllerTypeEnum getControllerType() {
        return controllerType;
    }

    public void setControllerType(MControllerTypeEnum controllerType) {
        this.controllerType = controllerType;
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

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getParentPropertyId() {
        if(parentProperty!=null) {
            parentPropertyId = parentProperty.getId();
        }
        return parentPropertyId;
    }

    public void setParentPropertyId(String parentPropertyId) {
        if(parentProperty!=null) {
            this.parentPropertyId = parentProperty.getId();
        }
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

    public Boolean getIsSearchCondition() {
        return isSearchCondition;
    }

    public void setIsSearchCondition(Boolean isSearchCondition) {
        this.isSearchCondition = isSearchCondition;
    }

    public String getBelongObjectId() {
        if(belongMObject!=null){
            belongObjectId = belongMObject.getId();
        }
        return belongObjectId;
    }

    public void setBelongObjectId(String belongObjectId) {
        if(belongMObject!=null){
            this.belongObjectId = belongMObject.getId();
        }
    }

    public String getPropertyObjectId() {
        return propertyObjectId;
    }

    public void setPropertyObjectId(String propertyObjectId) {
        this.propertyObjectId = propertyObjectId;
    }

    public String getRootObjectId() {
        if(rootMObject!=null){
            rootObjectId = rootMObject.getId();
        }
        return rootObjectId;
    }

    public void setRootObjectId(String rootObjectId) {
        if(rootMObject!=null){
            this.rootObjectId = rootMObject.getId();
        }
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MirrorPropertyEntity that = (MirrorPropertyEntity) o;

        if (deep != null ? !deep.equals(that.deep) : that.deep != null) return false;
        return !(propertyPath != null ? !propertyPath.equals(that.propertyPath) : that.propertyPath != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (deep != null ? deep.hashCode() : 0);
        result = 31 * result + (propertyPath != null ? propertyPath.hashCode() : 0);
        return result;
    }
}
