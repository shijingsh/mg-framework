package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.MTemplateService;
import com.mg.common.metadata.service.MetaDataManageService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
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
 * 元数据模板设置
 * @author liukefu
 *
 */
@Controller
@RequestMapping(value = "/metadata",produces = "application/json; charset=UTF-8")
public class MTemplateController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public MetaDataManageService metaDataManageService;
    @Autowired
    public MetaDataQueryService metaDataQueryService;
    @Autowired
    MTemplateService templateService;

    /**
     * 对象的模板列表
     * @param id   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/template/list")
    public String templateList(String  id) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(id);
        //有序模板列表
        List<MTemplateEntity> templates = templateService.findTemplateAll(mObjectEntity);

        return JsonResponse.success(templates, null);
    }

    /**
     * 对象的查看类模板列表
     * @param id   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/template/tabsList")
    public String templateTabList(String  id) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(id);
        //有序模板列表
        List<MTemplateEntity> templates = templateService.getTemplates(mObjectEntity, MTemplateTypeEnum.DataView);

        return JsonResponse.success(templates, null);
    }
    /**
     * 刷新元数据镜像
     * @param id   请求
     * @return          templateEntity
     */
    @ResponseBody
    @RequestMapping("/template/get")
    public String getTemplate(String  id) {

        MTemplateEntity templateEntity = templateService.findTemplateById(id);
        templateEntity.setTemplateStr(templateEntity.getTemplateSource());
        return JsonResponse.success(templateEntity, null);
    }
    /**
     * 刷新元数据镜像
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/template/post")
    public String postTemplate(HttpServletRequest req,String objId) {
        String jsonString = WebUtil.getJsonBody(req);
        MTemplateEntity templateEntity = JSON.parseObject(jsonString, MTemplateEntity.class);

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        templateEntity.setBelongMObject(mObjectEntity);

        templateService.saveTemplate(templateEntity);
        return JsonResponse.success(null, null);
    }
}
