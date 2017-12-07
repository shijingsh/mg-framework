package com.mg.framework.entity.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据 关系表达式
 * @author liukefu
 */
@Entity
@Table(name="sys_meta_express")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MExpressionEntity extends BaseEntity {

    /**
     * 上级表达式
     */
    @ManyToOne()
    @JoinColumn(name = "parent_express_id")
    @JSONField(serialize = false, deserialize = false)
    private MExpressionEntity parentExpression;
    /**
     * true 匹配全部 false 匹配任一
     */
    private Boolean isMatchAll = true;
    /**
     * 是否为一个节点
     */
    private Boolean isNode = false;
    /**
     * 表达式左值
     * 固定为元数据
     */
    @ManyToOne()
    @JoinColumn(name = "m_property_id")
    @NotFound(action= NotFoundAction.IGNORE)
    private MirrorPropertyEntity property;

    /**
     * 表达式关系
     * 见：MRelationEnum
     */
    private Integer relation;
    /**
     * 表达式值类型
     */
    @Enumerated(EnumType.STRING)
    private MValueType valueType = MValueType.CONST;

    /**
     * 元数据函数
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "[function]")
    private MFunction function;
    /**
     * 表达式右值
     * 根据valueType不同，value含义不同
     * 包括：常量、元数据值、动态脚本
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String value;

    /**
     * 表达式右值 的中文解释
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String valueChinese;

    @OneToMany(mappedBy = "parentExpression", cascade = CascadeType.ALL)
    private List<MExpressionEntity> expressions = new ArrayList<MExpressionEntity>();

    public MExpressionEntity() {

    }
    public MExpressionEntity(MirrorPropertyEntity property, Integer relation, String value) {
        this.property = property;
        this.relation = relation;
        this.value = value;
    }

    public MExpressionEntity(MirrorPropertyEntity property, Integer relation, String value, MValueType valueType) {
        this.value = value;
        this.property = property;
        this.relation = relation;
        this.valueType = valueType;
    }

    public MExpressionEntity(boolean isMatchAll, boolean isNode) {
        this.isMatchAll = isMatchAll;
        this.isNode = isNode;
    }

    public MExpressionEntity(MirrorPropertyEntity property, boolean isMatchAll, boolean isNode ) {
        this.isMatchAll = isMatchAll;
        this.isNode = isNode;
        this.property = property;
    }

    public MExpressionEntity getParentExpression() {
        return parentExpression;
    }

    public void setParentExpression(MExpressionEntity parentExpression) {
        this.parentExpression = parentExpression;
    }

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public MValueType getValueType() {
        return valueType;
    }

    public void setValueType(MValueType valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<MExpressionEntity> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<MExpressionEntity> expressions) {
        this.expressions = expressions;
    }

    public void addExpressions(MExpressionEntity expression) {
        this.expressions.add(expression);
    }

    public Boolean getIsMatchAll() {
        return isMatchAll;
    }

    public void setIsMatchAll(Boolean isMatchAll) {
        this.isMatchAll = isMatchAll;
    }

    public Boolean getIsNode() {
        return isNode;
    }

    public void setIsNode(Boolean isNode) {
        this.isNode = isNode;
    }

    public String getValueChinese() {
        return valueChinese;
    }

    public void setValueChinese(String valueChinese) {
        this.valueChinese = valueChinese;
    }

    public MFunction getFunction() {
        return function;
    }

    public void setFunction(MFunction function) {
        this.function = function;
    }
}
