package com.mg.report.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 维度小项
 * Created by liukefu on 2015/10/24.
 */
@Entity
@Table(name="report_dimen_item")
public class ReportDimenItemEntity extends BaseEntity {
    /**
     * 所属报表
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_report_id")
    private ReportEntity belongReport;
    /**
     * 所属维度
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_dimen_id")
    private ReportDimenEntity belongDimen;

    /**
     * 小项对应元数据
     */
    @ManyToOne
    @JoinColumn(name = "property_id")
    @NotFound(action= NotFoundAction.IGNORE)
    private MirrorPropertyEntity property;

    /**
     * 统计限定字段
     */
    @ManyToOne
    @JoinColumn(name = "property_scope_id")
    @NotFound(action= NotFoundAction.IGNORE)
    private MirrorPropertyEntity propertyScope;
    /**
     * 小项显示名称
     */
    private String aliasName;

    /**
     * 小项级别
     */
    private int dimenLev = 1;
    /**
     * 枚举类型、对象类型，固定值类型
     * 枚举类型、对象类型 ";" 分隔，固定值则没有分号
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String selectScope;
    /**
     * id范围时，存储id对应的名称
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String selectNames;
    /**
     * 区间范围（从）  数值型用 *100000000
     */
    private Double numFrom;
    private Double numTo;
    /**
     * 区间范围（从）  日期型用
     */
    private String dateFrom;
    private String dateTo;

    /**
     * 条件组ID
     */
    private String expressId;

    /**
     * 小项排序
     */
    private int sort;

    /**
     * 1 :指定区间,2:相对区间,3:等于 4:模糊匹配
     */
    private int dataType = 3;

    /**
     * 小项是个叶子节点
     */
    private Boolean isLeaf = false;
    /**
     * 小项下的子维度
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongItem")
    List<ReportDimenEntity> dimenList = new ArrayList<>();

    public ReportEntity getBelongReport() {
        return belongReport;
    }

    public void setBelongReport(ReportEntity belongReport) {
        this.belongReport = belongReport;
    }

    public ReportDimenEntity getBelongDimen() {
        return belongDimen;
    }

    public void setBelongDimen(ReportDimenEntity belongDimen) {
        this.belongDimen = belongDimen;
    }

    public void setDimenList(List<ReportDimenEntity> dimenList) {
        this.dimenList.clear();
        this.dimenList = dimenList;
    }

    public int getDimenLev() {
        return dimenLev;
    }

    public void setDimenLev(int dimenLev) {
        this.dimenLev = dimenLev;
    }

    public List<ReportDimenEntity> getDimenList() {
        return dimenList;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getSelectScope() {
        return selectScope;
    }

    public void setSelectScope(String selectScope) {
        this.selectScope = selectScope;
    }

    public Double getNumFrom() {
        return numFrom;
    }

    public void setNumFrom(Double numFrom) {
        this.numFrom = numFrom;
    }

    public Double getNumTo() {
        return numTo;
    }

    public void setNumTo(Double numTo) {
        this.numTo = numTo;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getExpressId() {
        return expressId;
    }

    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getSelectNames() {
        return selectNames;
    }

    public void setSelectNames(String selectNames) {
        this.selectNames = selectNames;
    }

    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public MirrorPropertyEntity getPropertyScope() {
        return propertyScope;
    }

    public void setPropertyScope(MirrorPropertyEntity propertyScope) {
        this.propertyScope = propertyScope;
    }
}
