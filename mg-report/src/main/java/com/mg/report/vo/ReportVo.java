package com.mg.report.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import com.mg.report.entity.ReportEntity;

import java.util.*;

/**
 * 报表显示vo
 * Created by liukefu on 2015/11/4.
 */
public class ReportVo implements java.io.Serializable {
    /**
     * 报表名称
     */
    private ReportEntity report;
    /**
     * 行维度
     */
    private TableHeaderCellVO rowDimen;
    /**
     * 显示列
     */
    private List<TableHeaderCellVO> showColumns;
    /**
     * 列维度
     */
    private List<TableHeaderCellVO> dimenColumns;
    /**
     * 表头
     */
    private List<List<TableHeaderCellVO>> headerColumns;
    /**
     * 数据列表
     */
    private Collection<ReportRowDataVo> list = new ArrayList<>();

    /**
     * 显示列中的叶子节点
     */
    private List<TableHeaderCellVO> leafColumns = new ArrayList<>();

    private int maxLevel;

    @JSONField(serialize = false, deserialize = false)
    private Map<String, ReportRowDataVo> rowDataMap = new HashMap<>();

    public ReportEntity getReport() {
        return report;
    }

    public void setReport(ReportEntity report) {
        this.report = report;
    }

    public TableHeaderCellVO getRowDimen() {
        return rowDimen;
    }

    public void setRowDimen(TableHeaderCellVO rowDimen) {
        this.rowDimen = rowDimen;
    }

    public List<TableHeaderCellVO> getShowColumns() {
        return showColumns;
    }

    public void setShowColumns(List<TableHeaderCellVO> showColumns) {
        this.showColumns = showColumns;
    }

    public List<TableHeaderCellVO> getDimenColumns() {
        return dimenColumns;
    }

    public void setDimenColumns(List<TableHeaderCellVO> dimenColumns) {
        this.dimenColumns = dimenColumns;
    }

    public Collection<ReportRowDataVo> getList() {
        return list;
    }

    public void setList(Collection<ReportRowDataVo> list) {
        this.list = list;
    }

    public List<List<TableHeaderCellVO>> getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(List<List<TableHeaderCellVO>> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public List<TableHeaderCellVO> getLeafColumns() {
        return leafColumns;
    }

    public void setLeafColumns(List<TableHeaderCellVO> leafColumns) {
        this.leafColumns = leafColumns;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Map<String, ReportRowDataVo> getRowDataMap() {
        return rowDataMap;
    }

    public void setRowDataMap(Map<String, ReportRowDataVo> rowDataMap) {
        this.rowDataMap = rowDataMap;
    }
}
