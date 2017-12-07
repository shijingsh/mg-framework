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
 * 报表维度
 * Created by liukefu on 2015/10/24.
 */
@Entity
@Table(name="report_dimen")
public class ReportDimenEntity extends BaseEntity {
    /**
     * 所属报表
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_report_id")
    private ReportEntity belongReport;
    /**
     * 所属小项
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_item_id")
    protected ReportDimenItemEntity belongItem;
    /**
     * 维度元数据
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
     * 统计方法
     */
    @Enumerated(EnumType.STRING)
    private StatisticalMethodEnum statisticalMethodEnum = StatisticalMethodEnum.COUNT;
    /**
     * 维度定义统计项
     */
    private String defTypeDisplay;
    /**
     * 第几维度
     */
    private int dimenNum;
    /**
     * 维度级别
     */
    private int dimenLev = 1;
    /**
     * 排序
     */
    private int dimenSort = 0;
    /**
     * 显示人员明细
     */
    private Boolean isDetail = false;
    /**
     * 默认展开
     */
    private Boolean isExpand = false;
    /**
     * 行维度合并
     */
    private Boolean isMerged = false;
    /**
     * 行维度，是否按需显示，默认显示所有行维度
     */
    private Boolean isAccordingNeed = false;
    /**
     * 维度下面的小项
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongDimen")
    List<ReportDimenItemEntity> itemList = new ArrayList<>();

    public ReportEntity getBelongReport() {
        return belongReport;
    }

    public void setBelongReport(ReportEntity belongReport) {
        this.belongReport = belongReport;
    }

    public ReportDimenItemEntity getBelongItem() {
        return belongItem;
    }

    public void setBelongItem(ReportDimenItemEntity belongItem) {
        this.belongItem = belongItem;
    }

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public String getDefTypeDisplay() {
        return defTypeDisplay;
    }

    public void setDefTypeDisplay(String defTypeDisplay) {
        this.defTypeDisplay = defTypeDisplay;
    }

    public int getDimenNum() {
        return dimenNum;
    }

    public void setDimenNum(int dimenNum) {
        this.dimenNum = dimenNum;
    }

    public int getDimenLev() {
        return dimenLev;
    }

    public void setDimenLev(int dimenLev) {
        this.dimenLev = dimenLev;
    }

    public int getDimenSort() {
        return dimenSort;
    }

    public void setDimenSort(int dimenSort) {
        this.dimenSort = dimenSort;
    }

    public Boolean getIsDetail() {
        return isDetail;
    }

    public void setIsDetail(Boolean isDetail) {
        this.isDetail = isDetail;
    }

    public Boolean getIsExpand() {
        return isExpand;
    }

    public void setIsExpand(Boolean isExpand) {
        this.isExpand = isExpand;
    }

    public Boolean getIsMerged() {
        return isMerged;
    }

    public void setIsMerged(Boolean isMerged) {
        this.isMerged = isMerged;
    }

    public Boolean getIsAccordingNeed() {
        return isAccordingNeed;
    }

    public void setIsAccordingNeed(Boolean isAccordingNeed) {
        this.isAccordingNeed = isAccordingNeed;
    }

    public List<ReportDimenItemEntity> getItemList() {
        return itemList;
    }

    public StatisticalMethodEnum getStatisticalMethodEnum() {
        return statisticalMethodEnum;
    }

    public void setStatisticalMethodEnum(StatisticalMethodEnum statisticalMethodEnum) {
        this.statisticalMethodEnum = statisticalMethodEnum;
    }

    public void setItemList(List<ReportDimenItemEntity> itemList) {
        this.itemList.clear();
        this.itemList = itemList;
    }

    public MirrorPropertyEntity getPropertyScope() {
        return propertyScope;
    }

    public void setPropertyScope(MirrorPropertyEntity propertyScope) {
        this.propertyScope = propertyScope;
    }
}
