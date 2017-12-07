package com.mg.report.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 查看报表的人员
 * Created by liukefu on 2015/10/24.
 */
@Entity
@Table(name="report_viewer")
public class ReportViewerEntity extends BaseEntity {
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_report_id")
    private ReportEntity belongReport;

    /**
     * 用户ID
     */
    private String userId;

    public ReportEntity getBelongReport() {
        return belongReport;
    }

    public void setBelongReport(ReportEntity belongReport) {
        this.belongReport = belongReport;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
