package com.mg.report.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.entity.metadata.MExpressionEntity;
import com.mg.framework.utils.JsonResponse;
import com.mg.groovy.util.CloneFilter;
import com.mg.groovy.util.HRMSBeanClone;
import com.mg.report.entity.*;
import com.mg.report.service.ReportDataService;
import com.mg.report.service.ReportService;
import com.mg.report.util.ReportExcelExport;
import com.mg.report.vo.ReportVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表统计
 * Created by liukefu on 2015/10/24.
 */
@Controller
@RequestMapping(value = "/report",produces = "application/json; charset=UTF-8")
public class ReportStatisticsController {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public ReportService reportService;
    @Autowired
    public ReportDataService reportDataService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    ReportExcelExport reportExcelExport;
    /**
     * 报表列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/list")
    public String list() {

        List<ReportEntity> list = reportService.findMyReports();
        return JsonResponse.success(list, null);
    }

    /**
     *  根据ID获取
     * @param id   请求
     * @return          ReportEntity
     */
    @ResponseBody
    @RequestMapping("/get")
    public String get(String  id) {

        ReportEntity report = reportService.findReport(id);
        return JsonResponse.success(report, null);
    }
    /**
     * 保存
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/post")
    public String postTemplate(HttpServletRequest req,String objId) {
        String jsonString = WebUtil.getJsonBody(req);

        ReportEntity report = JSON.parseObject(jsonString, ReportEntity.class);
        report.getColumnDimens().clear();
        report.getColumnDimens().addAll(report.getDimenList());

        List<CloneFilter> filterList = new ArrayList<>();
        filterList.add(new CloneFilter(ReportViewerEntity.class,"id"));
        filterList.add(new CloneFilter(ReportColumnEntity.class,"id"));
        filterList.add(new CloneFilter(ReportDimenEntity.class,"id"));
        filterList.add(new CloneFilter(ReportDimenItemEntity.class,"id"));
        filterList.add(new CloneFilter(MExpressGroupEntity.class,"id"));
        filterList.add(new CloneFilter(MExpressionEntity.class, "id"));
        filterList.add(new CloneFilter(MObjectEntity.class, "templates"));
        try {
            report = (ReportEntity)HRMSBeanClone.deepClone(report,filterList);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        reportService.saveReport(report);

        return JsonResponse.success(report.getId(), null);
    }

    /**
     * 删除报表功能
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete")
    public String delete(String  id) {

        reportService.delete(id);
        return JsonResponse.success(null, null);
    }

    @ResponseBody
    @RequestMapping("/rowDimen")
    public String rowDimen(String objectId) {
        if(StringUtils.isBlank(objectId)){
            List<MObjectEntity> list =  metaDataQueryService.findMObjectByModuleName(MetaDataUtils.DEFAULT_MODULE_NAME);
            if(list!=null && list.size()>0){
                MObjectEntity empObject = list.get(0);
                objectId = empObject.getId();
            }
        }
        List<MirrorPropertyEntity> list = reportService.findRowDimen(objectId);
        return JsonResponse.success(list, null);
    }

    /**
     * 获取报表数据
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getData")
    public String getData(String  id) {
        ReportVo reportVo = reportDataService.queryReportData(id);
        return JsonResponse.successWithDate(reportVo, "yyyy-MM-dd");
    }

    /**
     * 导出报表数据
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/exportData")
    public String exportData(String  id) {
        ReportVo reportVo = reportDataService.queryReportData(id);

        String path = reportExcelExport.expExcel(reportVo);
        return JsonResponse.success(path, null);
    }
}
