package com.mg.report.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.model.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 报表显示列
 * Created by liukefu on 2015/10/24.
 */
@Entity
@Table(name="report_column")
public class ReportColumnEntity extends BaseEntity {
    /**
     * 所属报表
     */
    @JSONField(serialize = false, deserialize = false)
    @ManyToOne
    @JoinColumn(name = "belong_report_id")
    private ReportEntity belongReport;
    /**
     * 基础元数据字段
     */
    @ManyToOne
    @JoinColumn(name = "property_id")
    @NotFound(action= NotFoundAction.IGNORE)
    private MirrorPropertyEntity property;
    /**
     * 列的别名
     */
    private String aliasName;
    /**
     * 列的自定义显示宽度
     */
    private Integer width;
    /**
     * 列的显示顺序
     */
    private Integer sort;

    public ReportEntity getBelongReport() {
        return belongReport;
    }

    public void setBelongReport(ReportEntity belongReport) {
        this.belongReport = belongReport;
    }

    public MirrorPropertyEntity getProperty() {
        return property;
    }

    public void setProperty(MirrorPropertyEntity property) {
        this.property = property;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
