package com.mg.report.service;

import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.report.entity.ReportDimenItemEntity;
import com.mg.report.entity.ReportEntity;

import java.util.List;

/**
 * Created by liukefu on 2015/10/24.
 */
public interface ReportService {

    /**
     * 保存报表
     * @param reportEntity
     */
    public void saveReport(ReportEntity reportEntity);

    /**
     * 查询报表
     * @param reportId
     * @return
     */
    public ReportEntity findReport(String reportId);

    /**
     * 查询我能查看的报表
     * @return
     */
    public List<ReportEntity> findMyReports();

    /**
     * 根据对象id，查询对象下面的行维度列表
     * @param objectId
     * @return
     */
    public List<MirrorPropertyEntity> findRowDimen(String objectId);

    /**
     * 获取报表中，最后一层的小项列表
     * 维度树形中的所有叶子节点
     * @param reportEntity
     */
    public List<ReportDimenItemEntity> getReportLastItemList(ReportEntity reportEntity);

    /**
     * 查询报表下的所有小项
     * @param report
     * @return
     */
    public List<ReportDimenItemEntity> findDimenItemsByReports(ReportEntity report);

    void delete(String id);
}
