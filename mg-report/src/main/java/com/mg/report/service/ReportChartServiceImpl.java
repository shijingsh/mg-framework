package com.mg.report.service;

import com.mg.report.dao.ReportChartDao;
import com.mg.report.dao.ReportDao;
import com.mg.report.entity.*;
import com.mg.report.vo.ChartDataVo;
import com.mg.report.vo.ReportChartData;
import com.mg.report.vo.ReportRowDataVo;
import com.mg.report.entity.ReportChartEntity;
import com.mg.report.entity.ReportDimenItemEntity;
import com.mg.report.vo.ReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 图形报表服务类
 * Created by liukefu on 2016/4/18.
 */
@Service
public class ReportChartServiceImpl implements ReportChartService {
    @Autowired
    public ReportService reportService;
    @Autowired
    public ReportDataService reportDataService;
    @Autowired
    private ReportChartDao reportChartDao;
    @Autowired
    ReportDao reportDao;

    @Transactional(readOnly = true)
    public ReportChartEntity findReport(String id) {

        return reportChartDao.findOne(id);
    }

    @Transactional
    public void saveReport(ReportChartEntity report) {

        ChartDataModelEnum chartDataModelEnum = getChartDataModel(report);
        report.setDataModelEnum(chartDataModelEnum);

        reportChartDao.saveAndFlush(report);
    }

    @Transactional(readOnly = true)
    public ReportChartData queryReportData(String id) {

        ReportChartEntity reportChartEntity = findReport(id);
        String reportId = reportChartEntity.getBelongReport().getId();
        ReportVo reportVo = reportDataService.queryReportData(reportId);

        List<ReportDimenItemEntity> itemEntities = reportService.findDimenItemsByReports(reportVo.getReport());
        //获取图表数据
        List<String> legendData = getLegendData(reportVo,itemEntities);
        List<String> rowDimen  = getRowDimenData(reportVo);
        List<?> seriesData = getSeriesData(reportChartEntity,reportVo,itemEntities);

        ReportChartData reportChartData = new ReportChartData();
        reportChartData.setLegendData(legendData);
        reportChartData.setReportChart(reportChartEntity);
        reportChartData.setRowDimen(rowDimen);
        reportChartData.setSeriesData(seriesData);

        return reportChartData;
    }

    public List<ReportChartEntity> findChartByReport(String id) {

        ReportEntity reportEntity = reportDao.findOne(id);
        List<ReportChartEntity> list = reportChartDao.findChartByBelongReport(reportEntity);
        return list;
    }

    @Transactional
    public void delete(String id) {
        reportChartDao.delete(id);
    }

    /**
     * 列维度的名称集合
     * @param reportVo
     * @return
     */
    private List<String> getLegendData(ReportVo reportVo,List<ReportDimenItemEntity> itemEntities){
        List<String> legendData = new ArrayList<>();

        for(ReportDimenItemEntity itemEntity:itemEntities){
            legendData.add(itemEntity.getAliasName());
        }
        return legendData;
    }
    /**
     * 行维度的名称集合
     * @param reportVo
     * @return
     */
    private List<String> getRowDimenData(ReportVo reportVo){
        List<String> rowDimenData = new ArrayList<>();
        Map<String, ReportRowDataVo> rowDataMap = reportVo.getRowDataMap();
        Iterator<String> iterator = rowDataMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            ReportRowDataVo rowDataVo = rowDataMap.get(key);
            rowDimenData.add(rowDataVo.getRowDimenName());
        }
        return rowDimenData;
    }
    /**
     * 图表数据的集合
     * @param reportVo
     * @return
     */
    private List<?> getSeriesData(ReportChartEntity reportChartEntity, ReportVo reportVo, List<ReportDimenItemEntity> itemEntities){

        ChartDataModelEnum dataModelEnum = getChartDataModel(reportChartEntity);
        switch (dataModelEnum){
            case simple:
                return getSeriesDataSimple(reportChartEntity, reportVo, itemEntities);
        }

        return getSeriesDataNormal(reportChartEntity,reportVo,itemEntities);
    }

    private List<?> getSeriesDataNormal(ReportChartEntity reportChartEntity, ReportVo reportVo, List<ReportDimenItemEntity> itemEntities){

        List<List<Object>> listAll = new ArrayList<>();
        Map<String, ReportRowDataVo> rowDataMap = reportVo.getRowDataMap();

        //所有的列维度
        for(ReportDimenItemEntity itemEntity:itemEntities){
            List<Object> rowDimenData = new ArrayList<>();
            //循环行维度
            Iterator<String> iterator = rowDataMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                ReportRowDataVo rowDataVo = rowDataMap.get(key);
                Object value = rowDataVo.getRowData().get(itemEntity.getId());
                if(value==null){
                    value = 0;
                }
                if(isCompositeValue(reportChartEntity)){
                    ChartDataVo chartDataVo = new ChartDataVo(itemEntity.getAliasName(),value);
                    rowDimenData.add(chartDataVo);
                }else{
                    rowDimenData.add(value);
                }
            }
            listAll.add(rowDimenData);
        }

        return listAll;
    }

    private List<?> getSeriesDataSimple(ReportChartEntity reportChartEntity, ReportVo reportVo, List<ReportDimenItemEntity> itemEntities){

        List<ChartDataVo> listAll = new ArrayList<>();
        Map<String, ReportRowDataVo> rowDataMap = reportVo.getRowDataMap();

        //所有的列维度
        for(ReportDimenItemEntity itemEntity:itemEntities){
            //循环行维度
            Iterator<String> iterator = rowDataMap.keySet().iterator();
            //只有一个行维度
            if (iterator.hasNext()) {
                String key = iterator.next();
                ReportRowDataVo rowDataVo = rowDataMap.get(key);
                Object value = rowDataVo.getRowData().get(itemEntity.getId());
                if(value==null){
                    value = 0;
                }
                ChartDataVo chartDataVo = new ChartDataVo(itemEntity.getAliasName(),value);
                listAll.add(chartDataVo);
            }
        }

        return listAll;
    }
    /**
     * 判断图表的值是否是复合类型的
     * @param reportChartEntity
     * @return
     */
    private boolean isCompositeValue(ReportChartEntity reportChartEntity){

        String chartType = reportChartEntity.getChartCategory().getType();
        switch (chartType){
            case "pie":
                return true;

        }
        return false;
    }


    /**
     * 获取图表的数据模型
     * @param reportChartEntity
     * @return
     */
    private ChartDataModelEnum getChartDataModel(ReportChartEntity reportChartEntity){

        String chartType = reportChartEntity.getChartCategory().getType();
        switch (chartType){
            case "pie":
                return ChartDataModelEnum.simple;

        }
        return ChartDataModelEnum.normal;
    }
}
