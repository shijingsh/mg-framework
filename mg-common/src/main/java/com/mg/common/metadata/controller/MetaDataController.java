package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.CustomFormService;
import com.mg.common.metadata.service.MEnumService;
import com.mg.common.metadata.service.MetaDataManageService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MPropertyFilter;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 元数据设置
 * @author liukefu
 *
 */
@Controller
@RequestMapping(value = "/metadata",produces = "application/json; charset=UTF-8")
public class MetaDataController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public MetaDataManageService metaDataManageService;
    @Autowired
    public MetaDataQueryService metaDataQueryService;
    @Autowired
    public MEnumService mEnumService;
    @Autowired
    CustomFormService customFormService;

    /**
     * 获取员工对象
     * @return
     */
    @ResponseBody
    @RequestMapping("/object/employee")
    public String empObject() {

        MObjectEntity objectEntity = metaDataQueryService.findEmployeeMObject();
        return JsonResponse.success(objectEntity, null);
    }
    /**
     * 元数据对象列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/list")
    public String listObject() {

        List<MObjectEntity> list = metaDataManageService.findAllObject();
        return JsonResponse.success(list, null);
    }


    /**
     * 元数据对象列表分页模式
     * @param req   请求
     * @return          PageTableVO
     */
    @ResponseBody
    @RequestMapping("/object/listPage")
    public String listPage(HttpServletRequest req) {
        String jsonString = WebUtil.getJsonBody(req);
        Map<String, Object> map = JSON.parseObject(jsonString);

        PageTableVO vo = metaDataManageService.findPageList(map);

        return JsonResponse.success(vo, null);
    }
    /**
     * 保存元数据对象列表
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/save")
    public String saveObject(HttpServletRequest req) {

        String jsonString = WebUtil.getJsonBody(req);
        MObjectEntity mObjectEntity = JSON.parseObject(jsonString, MObjectEntity.class);
        metaDataManageService.saveObject(mObjectEntity);

        return JsonResponse.success(null, null);
    }

    /**
     * 查询元数据对象
     * @param id   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/get")
    public String getObject(String  id) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(id);

        return JsonResponse.success(mObjectEntity, null);
    }


    /**
     * 查询元数据对象
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/getByName")
    public String getObjectByName(HttpServletRequest req) {
        String jsonString = WebUtil.getJsonBody(req);
        MObjectEntity mObjectEntity = JSON.parseObject(jsonString, MObjectEntity.class);
        MObjectEntity objectEntity = metaDataQueryService.findMObjectByName(mObjectEntity.getName());

        return JsonResponse.success(objectEntity, null);
    }
    /**
     * 刷新元数据镜像
     * @param id   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/mirror")
    public String refreshMirrorProperty(String  id) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(id);
        metaDataManageService.createMirrorProperties(mObjectEntity);

        return JsonResponse.success(null, null);
    }

    /**
     * 刷新整个模块下面的元数据镜像
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/objectAll/mirror")
    public String refreshAllMirrorProperty(String moduleName) {

        List<MObjectEntity> listObjs;
        if(StringUtils.isBlank(moduleName)){
            listObjs = metaDataQueryService.findAll();
        }else{
            listObjs = metaDataQueryService.findMObjectByModuleName(moduleName);
        }
        for(MObjectEntity mObjectEntity:listObjs){
            metaDataManageService.createMirrorProperties(mObjectEntity);
        }

        return JsonResponse.success(null, null);
    }

    /**
     * 刷新整个模块下面的元数据对象对应的表结构
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/object/tables")
    public String refreshTables(String moduleName) {

        List<MObjectEntity> listObjs = metaDataQueryService.findMObjectByModuleName(moduleName);
        for(MObjectEntity mObjectEntity:listObjs){
            metaDataManageService.refreshTables(mObjectEntity);
        }

        return JsonResponse.success(null, null);
    }
    /**
     * 元数据列表
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/property/list")
    public String listProperty(HttpServletRequest req,String objectId) {
        List<MPropertyEntity> list = null;
        if(StringUtils.isNotBlank(objectId)){
            MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objectId);
            list = metaDataManageService.findAllProperty(mObjectEntity);
        }else{
            list = metaDataManageService.findAllProperty();
        }

        return JsonResponse.success(list, null);
    }

    /**
     * 保存元数据对象列表
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/property/save")
    public String saveProperty(HttpServletRequest req) {

        String jsonString = WebUtil.getJsonBody(req);

        MPropertyEntity mPropertyEntity = JSON.parseObject(jsonString, MPropertyEntity.class);
        String objectId = mPropertyEntity.getBelongMObject().getId();
        if(mPropertyEntity.getBelongMObject()!=null&&StringUtils.isNotBlank(objectId)){
            MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objectId);
            mPropertyEntity.setBelongMObject(mObjectEntity);
        }
        metaDataManageService.saveProperty(mPropertyEntity);

        return JsonResponse.success(null, null);
    }

    /**
     * 查询元数据
     * @param id   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/property/get")
    public String getProperty(String  id) {

        MPropertyEntity mObjectEntity = metaDataManageService.findMPropertyById(id);

        return JsonResponse.success(mObjectEntity, null);
    }

    /**
     * 元数据列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/mirror/list")
    public String listMetadatas(String objectId) {
        MObjectEntity mObjectEntity = null;
        if(StringUtils.isBlank(objectId)){
            List<MObjectEntity> list = customFormService.findModuleObjects(MetaDataUtils.DEFAULT_MODULE_NAME);
            if(list!=null && list.size()>0){
                mObjectEntity = list.get(0);
            }
        }else{
            mObjectEntity = metaDataQueryService.findMObjectById(objectId);
        }
        List<MirrorPropertyEntity> list = metaDataQueryService.findMPropertyByRootMObject(mObjectEntity);
        List<MirrorPropertyEntity> listVisible = MPropertyFilter.showListProperties(list,true);

        String[] filter = {"templates"};
        return JsonResponse.success(listVisible, filter);
    }

    /**
     * 元数据依赖的枚举列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/mirror/enums")
    public String listEnum(String objectId) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objectId);
        List<MirrorPropertyEntity> list = metaDataQueryService.findMPropertyByRootMObject(mObjectEntity);
        List<MirrorPropertyEntity> listVisible = new ArrayList<>();
        for(MirrorPropertyEntity mirrorPropertyEntity:list){
            if(!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())){
                listVisible.add(mirrorPropertyEntity);
            }
        }
        Map<String,List<MEnumEntity>> map = mEnumService.findByProperties(listVisible);
        return JsonResponse.success(map, null);
    }

    /**
     * 根据元数据对象的id,返回默认的检索条件列表
     * @param objectId
     * @return
     */
    @RequestMapping(value = "/mirror/listCondition", method = RequestMethod.POST)
    @ResponseBody
    public String listCondition(String objectId,Integer maxLength) {

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objectId);
        if(maxLength==null || maxLength <= 0){
            maxLength = 4;
        }
        List<MirrorPropertyEntity> list = metaDataQueryService.getSearchConditionListProperties(metaObject, maxLength);
        return JsonResponse.success(list, null);
    }

    /**
     * 根据元数据对象的id,返回默认的检索条件列表
     * @param objectId
     * @return
     */
    @RequestMapping(value = "/mirror/listImport", method = RequestMethod.POST)
    @ResponseBody
    public String listImport(String objectId,Integer maxLength) {

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objectId);
        if(maxLength==null || maxLength <= 0){
            maxLength = 10;
        }
        List<MirrorPropertyEntity> list = metaDataQueryService.getImportProperties(metaObject, maxLength);
        return JsonResponse.success(list, null);
    }


    /**
     * 根据元数据对象的id和元数据类型,返回元数据列表
     * @param objectId
     * @return
     */
    @RequestMapping(value = "/mirror/controllerType", method = RequestMethod.POST)
    @ResponseBody
    public String listControllerType(String objectId,MControllerTypeEnum controllerType) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objectId);

        List<MirrorPropertyEntity> list = metaDataQueryService.findMPropertyByBelongMObjectAndControllerType(mObjectEntity, controllerType);
        List<MirrorPropertyEntity> listVisible = new ArrayList<>();
        for(MirrorPropertyEntity mirrorPropertyEntity:list){
            if(!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())) {
                listVisible.add(mirrorPropertyEntity);
            }
        }
        String[] filter = {"templates"};
        return JsonResponse.success(listVisible, filter);
    }
    /**
     * 从实体类创建元数据
     * @param entityName
     * @return
     */
    @RequestMapping(value = "/mirror/createFromEntity", method = RequestMethod.POST)
    @ResponseBody
    public String createFromEntity(String entityName) {

        try {
            Class clazz = Class.forName(entityName);
            metaDataManageService.createMObjectFromEntityClass(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return JsonResponse.error(1, "找不到实体类！");
        }

        return JsonResponse.success(null, null);
    }

    /**
     * 生成元数据的元数据
     * @return
     */
    @RequestMapping(value = "/mirror/createSelf", method = RequestMethod.POST)
    @ResponseBody
    public String createSelf() {

        metaDataManageService.createSelf();

        return JsonResponse.success(null, null);
    }

    /**
     * 获取生成元数据表结构开关的状态
     * @return
     */
    @RequestMapping(value = "/ddl/get", method = RequestMethod.POST)
    @ResponseBody
    public String getGenerateDDL() {

        return JsonResponse.success(MetaDataUtils.META_GENGERATE_DDL, null);
    }
    /**
     * 打开生成元数据表结构开关，默认是开的
     * @return
     */
    @RequestMapping(value = "/ddl/on", method = RequestMethod.POST)
    @ResponseBody
    public String openGenerateDDL() {

        MetaDataUtils.META_GENGERATE_DDL = true;

        return JsonResponse.success(null, null);
    }

    /**
     * 关闭生成元数据表结构开关
     * @return
     */
    @RequestMapping(value = "/ddl/off", method = RequestMethod.POST)
    @ResponseBody
    public String closeGenerateDDL() {

        MetaDataUtils.META_GENGERATE_DDL = false;

        return JsonResponse.success(null, null);
    }
}
