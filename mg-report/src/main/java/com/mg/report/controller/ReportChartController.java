package com.mg.report.controller;

import com.alibaba.fastjson.JSON;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import com.mg.report.entity.ReportChartEntity;
import com.mg.report.entity.ReportEntity;
import com.mg.report.service.ReportChartService;
import com.mg.report.vo.ReportChartData;
import com.mg.report.service.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图表接口
 * Created by liukefu on 2016/4/18.
 */
@Controller
@RequestMapping(value = "/chart",produces = "application/json; charset=UTF-8")
public class ReportChartController {

    @Autowired
    public ReportService reportService;
    @Autowired
    public ReportChartService reportChartService;
    /**
     *  根据ID获取
     * @param id   请求
     * @return
     */
    @ResponseBody
    @RequestMapping("/get")
    public String get(String  id) {

        ReportChartEntity report = reportChartService.findReport(id);
        return JsonResponse.success(report, null);
    }
    /**
     * 保存
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/post")
    public String post(HttpServletRequest req) {

        String jsonString = WebUtil.getJsonBody(req);
        ReportChartEntity report = JSON.parseObject(jsonString, ReportChartEntity.class);

        if(report.getBelongReport()!=null && StringUtils.isNotBlank(report.getBelongReport().getId())){
            ReportEntity reportEntity = reportService.findReport(report.getBelongReport().getId());
            report.setBelongReport(reportEntity);
        }

        reportChartService.saveReport(report);

        return JsonResponse.success(report.getId(), null);
    }

    /**
     * 删除
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/delete")
    public String delete(String id) {

        reportChartService.delete(id);

        return JsonResponse.success(null, null);
    }
    /**
     * 图表列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/list")
    public String list(String  id) {

        List<ReportChartEntity> list = reportChartService.findChartByReport(id);
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
        ReportChartData reportVo = reportChartService.queryReportData(id);
        return JsonResponse.successWithDate(reportVo, "yyyy-MM-dd");
    }
}
