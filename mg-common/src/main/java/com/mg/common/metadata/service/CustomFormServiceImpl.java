package com.mg.common.metadata.service;

import com.alibaba.fastjson.JSONObject;
import com.mg.common.metadata.util.MetaExcelExport;
import com.mg.common.metadata.util.MPropertyFilter;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.metadata.vo.SimpleExportVo;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.entity.vo.TableHeaderCellVO;
import com.mg.framework.utils.RequestHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义表单服务
 */
@Service
public class CustomFormServiceImpl implements CustomFormService {
    private static Logger logger = LoggerFactory.getLogger(CustomFormServiceImpl.class);
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MEnumService mEnumService;
    @Autowired
    private MetaExcelExport metaExcelExport;
    @Autowired
    private MObjectScriptService mObjectScriptService;
    @Autowired
    private MetaDataExpressService metaDataExpressService;
    @Autowired
    private TemplateExportService templateExportService;

    /**
     * 查询模块下维护的对象
     *
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findModuleObjects(String moduleName) {

        return metaDataQueryService.findMObjectManagedByModuleName(moduleName);
    }

    /**
     * 查询对象数据列表
     *
     * @param expressGroupEntity
     * @return
     */
    public PageTableVO findObjectsList(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity) {

        PageTableVO vo = new PageTableVO();
        List<MirrorPropertyEntity> mPropertyEntityList = null;
        boolean needColumnSort = true;
        if (expressGroupEntity.getExtendData() != null) {
            List<JSONObject> jsonList = (List<JSONObject>) expressGroupEntity.getExtendData();
            mPropertyEntityList = new ArrayList<>();
            for (JSONObject jsonObject : jsonList) {
                MirrorPropertyEntity propertyEntity = JSONObject.toJavaObject(jsonObject, MirrorPropertyEntity.class);
                propertyEntity = metaDataQueryService.findMPropertyById(propertyEntity.getId());
                if (propertyEntity != null) {
                    mPropertyEntityList.add(propertyEntity);
                }
            }
            //手工指定的显示字段，不按规则排序
            needColumnSort = false;
        } else {
            mPropertyEntityList = metaDataQueryService.findMPropertyNormalByBelongMObject(metaObject);
        }
        //设置排序字段
        initSort(metaObject, expressGroupEntity);
        //数据列表
        List<Map<String, Object>> list = metaDataService.queryByMetaData(metaObject, mPropertyEntityList, expressGroupEntity);
        //总记录数
        Integer totalCount = metaDataService.queryCountByMetaData(metaObject, mPropertyEntityList, expressGroupEntity);
        vo.setRowData(list);
        vo.setPageSize(expressGroupEntity.getPageSize());
        vo.setPageNo(expressGroupEntity.getPageNo());
        vo.setTotalCount(totalCount);
        //列表中显示的列集合
        List<MirrorPropertyEntity> columnProperties = MPropertyFilter.showVisibleProperties(mPropertyEntityList, MTemplateTypeEnum.DataList, -1, needColumnSort);
        vo.setColumns(getColumn4Table(columnProperties, expressGroupEntity));
        vo.setExtendData(columnProperties);
        return vo;
    }

    public void initSort(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity) {
        List<MOrderBy> orderByList = expressGroupEntity.getOrderByList();
        for (MOrderBy orderBy : orderByList) {
            if (orderBy.getProperty() != null && StringUtils.isNotBlank(orderBy.getProperty().getPropertyPath())) {

                MirrorPropertyEntity propertyEntity = metaDataQueryService.findMPropertyByRootMObjectAndPath(metaObject, orderBy.getProperty().getPropertyPath());
                if (propertyEntity != null) {
                    orderBy.setProperty(propertyEntity);
                }
            }
        }
    }

    private List<TableHeaderCellVO> getColumn4Table(List<MirrorPropertyEntity> columnProperties, MExpressGroupEntity expressGroupEntity) {
        List<TableHeaderCellVO> columns = new ArrayList<>();
        boolean hasLinkField = false;

        for (MirrorPropertyEntity propertyEntity : columnProperties) {
            TableHeaderCellVO col = new TableHeaderCellVO(propertyEntity.getPropertyPath(), propertyEntity.getName(), "text");
            if (StringUtils.isNotBlank(propertyEntity.getEnumName())) {
                List<MEnumEntity> temList = mEnumService.findByEnumName(propertyEntity.getEnumName());
                //放入枚举值
                col.setFilter(temList);
            }
            if (propertyEntity.getControllerType() == MControllerTypeEnum.bool) {
                MEnumEntity no = new MEnumEntity("0", "否");
                MEnumEntity yes = new MEnumEntity("1", "是");
                List<MEnumEntity> temList = new ArrayList<>();
                temList.add(yes);
                temList.add(no);
                col.setFilter(temList);
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.image) {
                col.setColumnType("images");
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.object) {
                col.setField(MetaDataUtils.getObjectFieldValue(propertyEntity));
            }
            if (StringUtils.equals(MetaDataUtils.META_FIELD_NAME, propertyEntity.getPropertyPath())) {
                col.setColumnType("custom");
                hasLinkField = true;
            } else if (StringUtils.equals(MetaDataUtils.META_FIELD_STATUS, propertyEntity.getPropertyPath())) {
                MEnumEntity no = new MEnumEntity("0", "无效");
                MEnumEntity yes = new MEnumEntity("1", "有效");
                List<MEnumEntity> temList = new ArrayList<>();
                temList.add(yes);
                temList.add(no);
                col.setFilter(temList);
            }
            metaDataExpressService.setTableHeaderSortType(expressGroupEntity, col);
            columns.add(col);
        }
        if (!hasLinkField && columns.size() > 0) {
            columns.get(0).setColumnType("custom");
        }
        return columns;
    }

    /**
     * 导出查询对象数据列表
     *
     * @param expressGroupEntity
     * @return
     */
    public String exportObjectsList(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity) {

        List<MirrorPropertyEntity> mPropertyEntityList = null;
        boolean needColumnSort = true;
        if (expressGroupEntity.getExtendData() != null) {
            List<?> jsonList = (List<?>) expressGroupEntity.getExtendData();
            mPropertyEntityList = new ArrayList<>();
            for (Object obj : jsonList) {
                MirrorPropertyEntity propertyEntity = null;
                if (MirrorPropertyEntity.class.isAssignableFrom(obj.getClass())) {
                    propertyEntity = (MirrorPropertyEntity) obj;
                    propertyEntity = metaDataQueryService.findMPropertyById(propertyEntity.getId());
                } else if (JSONObject.class.isAssignableFrom(obj.getClass())) {
                    JSONObject jsonObject = (JSONObject) obj;
                    propertyEntity = JSONObject.toJavaObject(jsonObject, MirrorPropertyEntity.class);
                    propertyEntity = metaDataQueryService.findMPropertyById(propertyEntity.getId());
                }
                if (propertyEntity != null) {
                    mPropertyEntityList.add(propertyEntity);
                }
            }
            needColumnSort = false;
        } else {
            mPropertyEntityList = metaDataQueryService.findMPropertyNormalByBelongMObject(metaObject);
        }
        //数据列表a
        expressGroupEntity.setPageSize(-1);
        //设置排序字段
        initSort(metaObject, expressGroupEntity);
        List<Map<String, Object>> list = metaDataService.queryByMetaData(metaObject, mPropertyEntityList, expressGroupEntity);

        //列表中显示的列集合
        List<MirrorPropertyEntity> columnProperties = MPropertyFilter.showVisibleProperties(mPropertyEntityList, MTemplateTypeEnum.DataList, -1, needColumnSort);
        String path = metaExcelExport.expExcel(columnProperties, list, metaObject.getName());
        return path;
    }


    /**
     * 根据模板导出查询的对象
     *
     * @param metaObject
     * @param templatePath
     * @param ids
     * @return
     */
    public String exportByTemplate(MObjectEntity metaObject, String templatePath, List<String> ids) {

        File file = new File(templatePath);
        if (!file.exists()) {
            //模板不存在时，根据母板创建模板
            String sourcePath = RequestHolder.getRequest().getSession()
                    .getServletContext().getRealPath("/WEB-INF/template/infoExport.xls");
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            createSourceTemplate(metaObject, templatePath, sourcePath);
        }

        String path = templateExportService.createExcel(metaObject, ids, templatePath);
        return path;
    }

    public void createSourceTemplate(MObjectEntity metaObject, String templatePath, String sourcePath) {
        //查询所有的字段
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(metaObject);
        List<MirrorPropertyEntity> columnProperties = MPropertyFilter.showVisibleProperties(mPropertyEntityList, MTemplateTypeEnum.DataView, -1, true);

        SimpleExportVo simpleExportVo = new SimpleExportVo();
        simpleExportVo.setName(metaObject.getName());
        int i = 0;
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        for (MirrorPropertyEntity propertyEntity : columnProperties) {

            if(i==2){
                i=0;
                list.add(map);
                map = new HashMap<>();
            }
            String key = getJxlsKey("obj."+propertyEntity.getPropertyPath());

            map.put("name"+(i+1),propertyEntity.getName());
            map.put("key"+(i+1),key);

            i++;
        }
        simpleExportVo.setList(list);

        Map<String, Object> beanParams = new HashMap<>();
        beanParams.put("obj",simpleExportVo);
        templateExportService.createExcel(sourcePath,beanParams,templatePath);
    }

    private String getJxlsKey(String name){
        return "${"+name+"}";
    }

    /**
     * 保存表单数据
     *
     * @param objId
     * @param dataMap
     * @return
     */
    @Transactional
    public String saveCustomFormData(String objId, Map<String, Object> dataMap) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        String id = metaDataService.saveByMetaData(objId, dataMap);

        //执行批量任务
        Map<String, Object> map = metaDataService.queryById(objId, id);
        mObjectScriptService.execObjectInsertTask(mObjectEntity, map);

        return id;
    }

    /**
     * 修改表单数据
     *
     * @param objId
     * @param dataMap
     * @return
     */
    @Transactional
    public int updateCustomFormData(String objId, Map<String, Object> dataMap) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        String id = (String) dataMap.get(MetaDataUtils.META_FIELD_ID);
        int count = 0;
        if (StringUtils.isNotBlank(id)) {
            List<MirrorPropertyEntity> mPropertModifiedList = metaDataService.updateMutiByMetaData(objId, dataMap);
            //找到修改的记录，作为触发脚本的数据参数
            Map<String, Object> map = metaDataService.queryById(objId, id);

            //执行批量任务
            mObjectScriptService.execObjectUpdateTask(mObjectEntity, mPropertModifiedList, map);
        }

        return count;
    }

    /**
     * 保存表单数据
     *
     * @param objId
     * @param dataMap
     * @return
     */
    public String saveData(String objId, Map<String, Object> dataMap) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        String id = metaDataService.saveObject(mObjectEntity, dataMap);

        //执行批量任务
        Map<String, Object> map = metaDataService.queryById(objId, id);
        mObjectScriptService.execObjectInsertTask(mObjectEntity, map);

        return id;
    }

    /**
     * 修改表单数据
     *
     * @param objId
     * @param dataMap
     * @return
     */
    public int updateData(String objId, Map<String, Object> dataMap) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        String id = (String) dataMap.get(MetaDataUtils.META_FIELD_ID);
        int count = 0;
        if (StringUtils.isNotBlank(id)) {
            List<MirrorPropertyEntity> mPropertModifiedList = metaDataService.updateMutiByMetaData(objId, dataMap);
            //找到修改的记录，作为触发脚本的数据参数
            Map<String, Object> map = metaDataService.queryById(objId, id);
            //执行批量任务
            mObjectScriptService.execObjectUpdateTask(mObjectEntity, mPropertModifiedList, map);
        }

        return count;
    }

    /**
     * 保存多条记录
     *
     * @param mpropertyId
     * @param dataMap
     * @return
     */
    @Transactional
    public String saveListCustomFormData(String mpropertyId, String mainObjId, Map<String, Object> dataMap[]) {

        MirrorPropertyEntity mProperty = metaDataQueryService.findMPropertyById(mpropertyId);
        //结构化对象的ID
        MObjectEntity mObjectEntity = mProperty.getMetaProperty().getMetaObject();
        String objId = mObjectEntity.getId();
        for (Map<String, Object> map : dataMap) {
            String id = (String) map.get(MetaDataUtils.META_FIELD_ID);
            if (StringUtils.isNotBlank(id)) {
                metaDataService.updateMutiByMetaData(objId, map);
            } else {
                //结构化对象
                List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByBelongMObject(mObjectEntity);
                //在关联关系中寻找与主对象之间的外键字段
                MPropertyEntity foreignkeyProperty = mProperty.getMetaProperty().getMappedByProperty();
                //设置外键的值
                if (foreignkeyProperty != null) {
                    for (MirrorPropertyEntity mPropertyEntity : mPropertyEntityList) {
                        if (StringUtils.equals(mPropertyEntity.getFieldName(), foreignkeyProperty.getFieldName())) {
                            map.put(mPropertyEntity.getFieldName(), mainObjId);
                        }
                    }
                }
                List<MirrorPropertyEntity> mSubPropertyList = metaDataQueryService.findMPropertyByBelongMObject(mObjectEntity);
                metaDataService.saveObject(mObjectEntity, mSubPropertyList, map);
            }
        }
        return "";
    }

    /**
     * 修改表单数据
     *
     * @param mPropertyId
     * @param objPKValue
     * @param dataMap
     * @return
     */
    @Transactional
    public String updateCustomFormData(String mPropertyId, String objPKValue, Map<String, Object> dataMap) {

        metaDataService.updateByMetaData(mPropertyId, objPKValue, dataMap);

        //执行批量任务
        MirrorPropertyEntity mPropertyEntity = metaDataQueryService.findMPropertyById(mPropertyId);
        MObjectEntity mObjectEntity = mPropertyEntity.getBelongMObject();
        Map<String, Object> map = metaDataService.queryById(mObjectEntity.getId(), objPKValue);
        mObjectScriptService.execObjectUpdateTask(mObjectEntity, mPropertyEntity, map);

        return objPKValue;
    }

    /**
     * 根据元数据对象id,设置对象数据无效
     *
     * @param objId
     * @param objPKValue
     * @return
     */
    @Transactional
    public String deleteCustomFormData(String objId, String objPKValue) {
/*        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        MirrorPropertyEntity mPropertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity, mObjectEntity, MetaDataUtils.META_FIELD_STATUS);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(mPropertyEntity.getPropertyPath(), StatusEnum.STATUS_INVALID);*/
        metaDataService.deleteById(objId, objPKValue);

        return objPKValue;
    }

    /**
     * 删除多条记录
     *
     * @param mpropertyId
     * @param dataMap
     * @return
     */
    @Transactional
    public String deleteListCustomFormData(String mpropertyId, Map<String, Object> dataMap[]) {
        MirrorPropertyEntity mProperty = metaDataQueryService.findMPropertyById(mpropertyId);
        //结构化对象的ID
        MObjectEntity mObjectEntity = mProperty.getMetaProperty().getMetaObject();
        String objId = mObjectEntity.getId();
        for (Map<String, Object> map : dataMap) {
            String id = (String) map.get(MetaDataUtils.META_FIELD_ID);
            if (StringUtils.isNotBlank(id)) {
                metaDataService.deleteById(objId, id);
            }
        }
        return "";
    }

    /**
     * 根据元数据对象名称和实体ID,获取对应的实体数据
     *
     * @param objId
     * @param id
     * @param ids
     * @param relationProperty 是否包含下级属性
     * @return
     */
    public Map<String, Object> getCustomFormData(String objId, String id, List<String> ids, boolean relationProperty) {
        if (null == ids) {
            return null;
        }
        List<MirrorPropertyEntity> list = metaDataQueryService.findByIds(ids);

        if (relationProperty) {
            return metaDataService.queryAllPropertiesById(objId, id);
        }
        return metaDataService.queryById(objId, id, list);
    }

    /**
     * 设置模板上的元数据属性
     *
     * @param objId
     * @param propList
     * @param modelMap
     */
    public void setCustomFormProperties(String objId, List<String> propList, ModelMap modelMap) {
        //MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        for (String prop : propList) {
            MirrorPropertyEntity propertyEntity = null;
            //处理关联属性的情况
            if (prop.contains("__")) {
                String[] mRelProps = StringUtils.split(prop, "__");

            } else {
                //propertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity,prop);
                propertyEntity = metaDataQueryService.findMPropertyById(prop);
            }
            if (propertyEntity != null) {
                modelMap.addAttribute(propertyEntity.getFieldName(), propertyEntity);
            }

        }
    }

    /**
     * 获取模板上的元数据枚举属性
     *
     * @param objId
     * @param propList
     */
    public Map<String, List<MEnumEntity>> getCustomFormEnum(String objId, List<String> propList) {
        //处理关联属性的情况
       /* MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        List<String> enumList = new ArrayList<>();*/
        Map<String, List<MEnumEntity>> map = new HashMap<>();
        for (String prop : propList) {
            MirrorPropertyEntity propertyEntity = metaDataQueryService.findMPropertyById(prop);
            if (propertyEntity != null && propertyEntity.getMetaProperty().getTypeEnum() == MTypeEnum.mEnum) {
                if (StringUtils.isNotBlank(propertyEntity.getEnumName())) {
                    List<MEnumEntity> temList = mEnumService.findByEnumName(propertyEntity.getEnumName());
                    map.put(propertyEntity.getPropertyPath(), temList);
                }
            } else if (propertyEntity.getControllerType() == MControllerTypeEnum.subType) {
                //结构化字段，下属字段可能是枚举类型
                MObjectEntity structObject = propertyEntity.getMetaProperty().getMetaObject();
                List<MirrorPropertyEntity> structProperties = metaDataQueryService.findMPropertyByRootMObject(structObject);
                for (MirrorPropertyEntity property : structProperties) {
                    if (property != null && property.getMetaProperty().getTypeEnum() == MTypeEnum.mEnum) {
                        if (StringUtils.isNotBlank(property.getEnumName())) {
                            List<MEnumEntity> temList = mEnumService.findByEnumName(property.getEnumName());
                            map.put(property.getPropertyPath(), temList);
                        }
                    }
                }
            }

        }

        return map;
    }
}
