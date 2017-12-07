package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.MetaDataImportService;
import com.mg.framework.entity.metadata.MImportTemplateEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据导入
 * @author liukefu
 *
 */
@Controller
@RequestMapping(value = "/metadata",produces = "application/json; charset=UTF-8")
public class MetaDataImportController {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public MetaDataImportService metaDataImportService;

    /**
     * 元数据导入
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/import/data")
    public String listObject(HttpServletRequest req,String path) {
        metaDataImportService.importData(path);
        return JsonResponse.success(null, null);
    }

    /**
     * 创建导入模板
     * @param req
     * @param objId
     * @return
     */
    @ResponseBody
    @RequestMapping("/import/createTemplate")
    public String createTemplate(HttpServletRequest req,String objId) {
        String jsonString = WebUtil.getJsonBody(req);
        MImportTemplateEntity template =  JSON.parseObject(jsonString, MImportTemplateEntity.class);
        metaDataImportService.createExcelTemplate(template,objId);
        metaDataImportService.saveImportTemplate(template,objId);
        return JsonResponse.success(null, null);
    }

    /**
     * 获取对象下面导入模板列表
     * @param req
     * @param objId
     * @return
     */
    @ResponseBody
    @RequestMapping("/import/templateList")
    public String listTemplates(HttpServletRequest req,String objId) {
        List<MImportTemplateEntity> list = metaDataImportService.queryImportTemplateList(objId);
        return JsonResponse.success(list, null);
    }
}
