package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.freeMarker.TemplateUtil;
import com.mg.common.metadata.service.CustomFormService;
import com.mg.common.metadata.service.MTemplateService;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.vo.MObjectExportVo;
import com.mg.common.entity.UserEntity;
import com.mg.common.user.service.UserService;
import com.mg.framework.utils.WebUtil;
import com.mg.common.utils.excel.DownExcelUtil;
import com.mg.framework.exception.ServiceException;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.sys.PropertyConfigurer;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import com.mg.framework.utils.UserHolder;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义表单
 *
 * @author liukefu
 */
@Controller
@RequestMapping(value = "/metadata/form", produces = "application/json; charset=UTF-8")
public class MetaDataFormController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    CustomFormService customFormService;
    @Autowired
    HttpServletRequest req;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MTemplateService mTemplateService;
    @Autowired
    MetaDataExpressService metaDataExpressService;
    @Autowired
    UserService userService;
    /**
     * 根据模块名称，查询模块下的所有维护对象
     *
     * @param moduleName
     * @return
     */
    @RequestMapping(value = "/objects", method = RequestMethod.POST)
    @ResponseBody
    public String listObj(String moduleName) {

        List<MObjectEntity> list = customFormService.findModuleObjects(moduleName);

        return JsonResponse.success(list, null);
    }

    /**
     * 根据id查询对象
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/object", method = RequestMethod.POST)
    @ResponseBody
    public String object(String id) {

        MObjectEntity employeeMObject;
        if(StringUtils.isBlank(id)){
            employeeMObject = metaDataQueryService.findEmployeeMObject();
        }else {
            employeeMObject = metaDataQueryService.findMObjectById(id);
        }

        return JsonResponse.success(employeeMObject, null);
    }
    /**
     * 根据元数据对象的id,查询对象数据列表
     *
     * @param objId
     * @return
     */
    @RequestMapping(value = "/listData", method = RequestMethod.POST)
    @ResponseBody
    public String listData(String objId) {
        String jsonString = WebUtil.getJsonBody(req);
        MExpressGroupEntity express = JSON.parseObject(jsonString, MExpressGroupEntity.class);

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objId);
        if (express == null || express.getMatched() == null
                || express.getMatched().getExpressions() == null
                || express.getMatched().getExpressions().size() == 0) {
            Integer pageSize = express.getPageSize();
            express = metaDataExpressService.createBlankExpressGroup(express);
            express.setPageSize(pageSize);
        }
        PageTableVO vo = customFormService.findObjectsList(metaObject, express);
        return JsonResponse.successWithDate(vo, "yyyy-MM-dd");
    }

    /**
     * 根据元数据对象的id条件组，导出数据列表
     *
     * @param objId
     * @return
     */
    @RequestMapping(value = "/exportListData")
    @ResponseBody
    public String exportListData(String objId, HttpServletResponse response) {
        String jsonString = WebUtil.getJsonBody(req);
        MExpressGroupEntity express = JSON.parseObject(jsonString, MExpressGroupEntity.class);

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objId);
        if (express == null || express.getMatched() == null
                || express.getMatched().getExpressions() == null
                || express.getMatched().getExpressions().size() == 0) {
            express = metaDataExpressService.createBlankExpressGroup(express);

        }
        String path = customFormService.exportObjectsList(metaObject, express);

        return JsonResponse.success(path, null);
    }

    /**
     * 根据导出模板，导出对象
     * @param objId
     * @return
     */
    @RequestMapping(value = "/exportByTemplate")
    @ResponseBody
    public String exportByTemplate(String objId) {
        String jsonString = WebUtil.getJsonBody(req);
        MObjectExportVo exportVo = JSON.parseObject(jsonString, MObjectExportVo.class);
        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objId);
        String token = UserHolder.getLoginUserTenantId();
        String objectId = metaObject.getId();
        String templatePath = req.getSession().getServletContext().getRealPath("/WEB-INF/template/"+token+"/"+objectId+"Export.xls");
        exportVo.setTemplatePath(templatePath);
        String path = customFormService.exportByTemplate(metaObject, exportVo.getTemplatePath(),exportVo.getIds());

        return JsonResponse.success(path, null);
    }

    /**
     * 下载导出数据列表
     *
     * @return
     */
    @RequestMapping(value = "/downExportListData")
    public void downExportListData(String path,HttpServletResponse response) {

        String fileName = String.valueOf(new Date().getTime())+".xls";
        DownExcelUtil.downFromTempPath(path, fileName, response);
    }

    /**
     * 根据元数据对象id,保存对象数据
     *
     * @param objId
     * @return
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public String postCustomFormData(String objId) {

        String jsonString = WebUtil.getJsonBody(req);
        Map dataMap = JSON.parseObject(jsonString, Map.class);
        try {
            String id = customFormService.saveCustomFormData(objId, dataMap);
            return JsonResponse.success(id, null);
        } catch (ServiceException e) {
            e.printStackTrace();
            return JsonResponse.error(1, e.getMessage());
        }
    }

    /**
     * 根据元数据对象id,保存多条对象数据
     *
     * @param mpropertyId 结构化字段元数据ID
     * @param mainObjId   主对象ID
     * @return
     */
    @RequestMapping(value = "/postList", method = RequestMethod.POST)
    @ResponseBody
    public String postListCustomFormData(String mpropertyId, String mainObjId) {

        String jsonString = WebUtil.getJsonBody(req);
        Map[] dataMap = JSON.parseObject(jsonString, Map[].class);
        try {
            String id = customFormService.saveListCustomFormData(mpropertyId, mainObjId, dataMap);
            return JsonResponse.success(id, null);
        } catch (ServiceException e) {
            e.printStackTrace();
            return JsonResponse.error(1, e.getMessage());
        }
    }

    /**
     * 根据元数据对象id,设置对象数据无效
     *
     * @param objId   主对象ID
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteCustomFormData(String objId,String pkId) {

        try {
            String id = customFormService.deleteCustomFormData(objId, pkId);
            return JsonResponse.success(id, null);
        } catch (ServiceException e) {
            e.printStackTrace();
            return JsonResponse.error(1, e.getMessage());
        }
    }
    /**
     * 根据元数据id,保存多条对象数据
     *
     * @param mpropertyId
     * @return
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST)
    @ResponseBody
    public String deleteListCustomFormData(String mpropertyId) {

        String jsonString = WebUtil.getJsonBody(req);
        Map[] dataMap = JSON.parseObject(jsonString, Map[].class);
        try {
            String id = customFormService.deleteListCustomFormData(mpropertyId, dataMap);
            return JsonResponse.success(id, null);
        } catch (ServiceException e) {
            e.printStackTrace();
            return JsonResponse.error(1, e.getMessage());
        }
    }

    /**
     * 根据对象数据ID,和元数据id,更新单个字段值
     *
     * @param objId
     * @param propertyId
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateCustomFormData(String objId, String propertyId) {

        String jsonString = WebUtil.getJsonBody(req);
        Map dataMap = JSON.parseObject(jsonString, Map.class);
        MirrorPropertyEntity mirrorPropertyEntity = metaDataQueryService.findMPropertyById(propertyId);
        Map propertyMap = new HashMap();
        propertyMap.put(mirrorPropertyEntity.getFieldName(), dataMap.get(mirrorPropertyEntity.getFieldName()));
        String id = customFormService.updateCustomFormData(propertyId, objId, propertyMap);

        return JsonResponse.success(id, null);
    }

    /**
     * 根据数据id,获取元数据对象里面数据
     *
     * @param objId      元数据对象id
     * @param templateId 模板ID
     * @param id         数据id
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public String getCustomFormData(String objId, String templateId, String id) {
        MTemplateEntity templateEntity = mTemplateService.findTemplateById(templateId);
        List<String> mPropNames = _getTemplateProperties(templateEntity, true);

        Map<String, Object> map = customFormService.getCustomFormData(objId, id, mPropNames, true);

        return JsonResponse.successWithDate(map, "yyyy-MM-dd");
    }

    /**
     * 根据对象id,模板id生成页面
     *
     * @param id         元数据对象ID
     * @param modelMap
     * @param objId      数据对象ID
     * @param templateId 模板ID
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/page")
    public String getTemplate(@RequestParam("id") String id, ModelMap modelMap, String objId, MTemplateTypeEnum templateType, String templateId) throws Exception {
        if (templateType == null) {
            templateType = MTemplateTypeEnum.DataEntry;
        }
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        MTemplateEntity templateEntity = null;
        if (StringUtils.isNotBlank(templateId)) {
            templateEntity = mTemplateService.findTemplateById(templateId);
        } else {
            templateEntity = mTemplateService.getTemplate(mObjectEntity, templateType);
        }
        List<String> propList = _getTemplateProperties(templateEntity, true);
        logger.debug("Data template properites: {} ", propList);
        customFormService.setCustomFormProperties(objId, propList, modelMap);

        modelMap.addAllAttributes(PropertyConfigurer.getConfigurer());
        modelMap.addAttribute("id", id);
        if (templateEntity == null) {
            return "";
        }
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("id", id);

        return TemplateUtil.getTemplateHtml(templateEntity.getTemplate(),rootMap);
    }

    /**
     * 获取元数据对象使用的模板ID
     *
     * @param objId        数据对象ID
     * @param templateType 模板类型
     * @param index        模板的索引
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/nextTemplate")
    public String pageTemplate(String objId, MTemplateTypeEnum templateType, Integer index) {
        if (templateType == null) {
            templateType = MTemplateTypeEnum.DataEntry;
        }
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        MTemplateEntity templateEntity = mTemplateService.getTemplate(mObjectEntity, templateType, index);

        return JsonResponse.success(templateEntity, null);
    }

    /**
     * 获取表单依赖的枚举对象
     *
     * @param objId
     * @param templateType
     * @param templateId   模板ID
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/pageEnum")
    public String getEnum(String objId, MTemplateTypeEnum templateType, String templateId) throws Exception {
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        MTemplateEntity templateEntity = null;
        if (StringUtils.isNotBlank(templateId)) {
            templateEntity = mTemplateService.findTemplateById(templateId);
        } else {
            templateEntity = mTemplateService.getTemplate(mObjectEntity, templateType);
        }
        List<String> propList = _getTemplateProperties(templateEntity, true);

        Map<String, List<MEnumEntity>> map = customFormService.getCustomFormEnum(objId, propList);
        return JsonResponse.success(map, null);
    }

    private List<String> _getTemplateProperties(MTemplateEntity templateEntity, boolean isIncludeSubProps) {

        List<String> propList = new ArrayList<>();
        if (templateEntity == null) {
            return propList;
        }
        Template template = null;
        try {
            template = TemplateUtil.createTemplate(templateEntity.getTemplate());
        } catch (Exception e) {
            logger.error("Can not parse template error {}", templateEntity.getId());
            e.printStackTrace();
        }

        //解析property属性的值//
        Pattern p = Pattern.compile("property=(\\w*)[\\s|\\/>]", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(template.toString());
        while (m.find()) {
            propList.add(m.group(1));
        }
        if (isIncludeSubProps) {
            //解析subProps属性的值，是一个列表//
            p = Pattern.compile("subProps=\\[([^]]*)\\]", Pattern.MULTILINE | Pattern.DOTALL);
            m = p.matcher(template.toString());
            while (m.find()) {
                propList.addAll(Arrays.asList(StringUtils.splitByWholeSeparator(m.group(1), ", ")));
            }
        }
        return propList;
    }


    /**
     * 获取个人信息页面
     * @return
     */
    @RequestMapping("/myInfo")
    public String myInfo() {
        UserEntity userEntity = UserHolder.getLoginUser();
        String id = userService.getEmployeeIdByUser(userEntity);
        MObjectEntity mObjectEntity = metaDataQueryService.findEmployeeMObject();
        String url = "/emp/empManage/empManage.jsp#/view/"+mObjectEntity.getId()+"/"+id;
        return "redirect:"+url;
    }
}
