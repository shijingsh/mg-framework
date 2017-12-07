package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

/**
 * 自定义表单服务
 */
public interface CustomFormService {
    /**
     * 查询对象数据列表
     * @param expressGroupEntity
     * @return
     */
    public PageTableVO findObjectsList(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity);

    /**
     * 导出查询对象数据列表
     * @param expressGroupEntity
     * @return
     */
    public String exportObjectsList(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity);

    /**
     * 根据模板导出查询的对象
     * @param metaObject
     * @param templatePath
     * @param ids
     * @return
     */
    public String exportByTemplate(MObjectEntity metaObject, String templatePath, List<String> ids);
    /**
     * 查询模块下维护的对象
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findModuleObjects(String moduleName);
    /**
     * 保存表单数据
     * @param objName
     * @param dataMap
     * @return
     */
    public String saveCustomFormData(String objName,  Map<String, Object> dataMap);

    /**
     * 修改表单数据
     * @param objId
     * @param dataMap
     * @return
     */
    public int updateCustomFormData(String objId,  Map<String, Object> dataMap);

    /**
     * 保存表单数据
     * @param objId
     * @param dataMap
     * @return
     */
    public String saveData(String objId,  Map<String, Object> dataMap);

    /**
     * 修改表单数据
     * @param objId
     * @param dataMap
     * @return
     */
    public int updateData(String objId,  Map<String, Object> dataMap);

    /**
     * 保存多条记录
     * @param objId
     * @param mainObjId
     * @param dataMap
     * @return
     */
    public String saveListCustomFormData(String objId,String mainObjId, Map<String, Object> dataMap[]);
    /**
     * 修改表单数据
     * @param mPropertyId
     * @param objPKValue
     * @param dataMap
     * @return
     */
    public String updateCustomFormData(String mPropertyId,String objPKValue,  Map<String, Object> dataMap);

    /**
     * 根据元数据对象id,设置对象数据无效
     * @param objId
     * @return
     */
    public String deleteCustomFormData(String objId,String objPKValue);

    /**
     * 删除多条记录
     * @param mpropertyId
     * @param dataMap
     * @return
     */
    public String deleteListCustomFormData(String mpropertyId,  Map<String, Object> dataMap[]);
    /**
     * 根据元数据对象名称和实体ID,获取对应的实体数据
     * @param objName
     * @param id
     * @param mPropNameList
     * @param relationProperty
     * @return
     */
    public Map<String, Object> getCustomFormData(String objName,String id,List<String> mPropNameList,boolean relationProperty);
    /**
     * 设置模板上的元数据属性
     * @param objName
     * @param propList
     * @param modelMap
     */
    public void setCustomFormProperties(String objName,List<String> propList,ModelMap modelMap);

    /**
     * 获取模板上的元数据枚举属性
     * @param objId
     * @param propList
     */
    public Map<String, List<MEnumEntity>> getCustomFormEnum(String objId, List<String> propList);

    /**
     *
     * @param metaObject
     * @param expressGroupEntity
     */
    public void initSort(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity);
}
