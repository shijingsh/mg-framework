package com.mg.report.entity;

import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.model.BaseEntity;
import com.mg.framework.utils.StatusEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liukefu on 2015/10/24.
 */
@Entity
@Table(name="report_info")
public class ReportEntity extends BaseEntity {
    /**
     * 报表统计的元数据对象
     */
    private String objectId;
    /**
     * 统计范围
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "express_id")
    protected MExpressGroupEntity expressGroup;
    /**
     * 报表名称
     */
    @Column(length = 500)
    private String name;
    /**
     * 所属用户ID
     */
    private String userId;
    /**
     * 有效状态
     */
    private Integer status = StatusEnum.STATUS_VALID;

    /**
     * 有权查看的人员
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongReport")
    List<ReportViewerEntity> viewerList = new ArrayList<>();

    /**
     * 报表显示列
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongReport")
    private List<ReportColumnEntity> columns = new ArrayList<>();

    /**
     * 维度集合
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "belongReport")
    private List<ReportDimenEntity> columnDimens  = new ArrayList<>();

    @Transient
    private String reportId;
    /**
     * 行维度
     */
    @Transient
    private ReportDimenEntity rowDimen = null;
    /**
     * 列维度集合
     */
    @Transient
    private List<ReportDimenEntity> dimenList  = new ArrayList<>();

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MExpressGroupEntity getExpressGroup() {
        return expressGroup;
    }

    public void setExpressGroup(MExpressGroupEntity expressGroup) {
        this.expressGroup = expressGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ReportViewerEntity> getViewerList() {
        return viewerList;
    }

    public void setViewerList(List<ReportViewerEntity> viewerList) {
        this.viewerList.clear();
        this.viewerList = viewerList;
    }

    public List<ReportColumnEntity> getColumns() {
        return columns;
    }

    public void setColumns(List<ReportColumnEntity> columns) {
        this.columns.clear();
        this.columns = columns;
    }

    public ReportDimenEntity getRowDimen() {
        return rowDimen;
    }

    public void setRowDimen(ReportDimenEntity rowDimen) {
        this.rowDimen = rowDimen;
    }

    public List<ReportDimenEntity> getColumnDimens() {
        return columnDimens;
    }

    public void setColumnDimens(List<ReportDimenEntity> columnDimens) {
        this.columnDimens.clear();
        this.columnDimens = columnDimens;
    }

    public List<ReportDimenEntity> getDimenList() {
        return dimenList;
    }

    public void setDimenList(List<ReportDimenEntity> dimenList) {
        this.dimenList = dimenList;
    }
}
