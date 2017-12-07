package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;

import java.util.List;

/**
 * 元数据查询
 */
public interface MetaDataQueryService {
    /**
     * 查询所有元数据对象
     * @return
     */
    public List<MObjectEntity> findAll();
    /**
     * 获取员工的元数据对象
     * @return
     */
    public MObjectEntity findEmployeeMObject();
    /**
     * 根据ID获取元数据对象
     * @param id
     * @return
     */
    public MObjectEntity findMObjectById(String id);
    /**
     * 根据名称获取元数据对象
     * @param name
     * @return
     */
    public MObjectEntity findMObjectByName(String name);

    /**
     * 根据模块查询下面维护的对象
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findMObjectByModuleName(String moduleName);
    /**
     * 根据模块查询下面维护的对象
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findMObjectManagedByModuleName(String moduleName);
    /**
     * 根据元数据ID查询元数据
     * @param mPropertyId
     * @return
     */
    public MirrorPropertyEntity findMPropertyById(String mPropertyId);

    /**
     * 根据元数据对象、元数据名称查询元数据
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByName(MObjectEntity belongMObject, String name);

    /**
     * 根据元数据对象，查询元数据列表
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyAllByBelongMObjectAndName(MObjectEntity belongMObject, String name);
    /**
     * 根据元数据对象，查询元数据列表
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndName(MObjectEntity rootMObject, MObjectEntity belongMObject, String name);

    /**
     * 根据元数据对象，查询元数据列表
     * @param belongMObject
     * @param names
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectAndNames(MObjectEntity rootMObject, MObjectEntity belongMObject, String[] names);
    /**
     * 根据元数据对象和元数据路径，查询元数据
     * @param rootMObject
     * @param path
     * @return
     */
    public MirrorPropertyEntity findMPropertyByRootMObjectAndPath(MObjectEntity rootMObject, String path);
    /**
     * 根据元数据对象，查询元数据列表
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndName(MObjectEntity belongMObject, String name);
    /**
     * 根据元数据对象和字段，查询元数据列表
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldName(MObjectEntity belongMObject, String fieldName);

    /**
     * 根据元数据主对象，对象和字段，查询元数据
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldNameAndDeep(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName, Integer deep);
    /**
     * 根据元数据主对象，对象和字段，查询元数据
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldName(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName);
    /**
     * 查找元数据对象的 PrimaryKey
     * @param belongMObject
     * @return
     */
    public MirrorPropertyEntity findPrimaryKeyMPropertyByBelongMObject(MObjectEntity belongMObject);
    /**
     * 根据元数据对象查询元数据
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObject(MObjectEntity belongMObject);

    /**
     * 根据元数据对象查询元数据
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectEditable(MObjectEntity belongMObject);

    /**
     * 根据元数据对象查询普通类型的元数据
     * 排除了对象类型的属性
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyNormalByBelongMObject(MObjectEntity belongMObject);
    /**
     * 根据元数据对象查询指定类型的元数据
     * @param belongMObject
     * @param controllerTypeEnum
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectAndControllerType(MObjectEntity belongMObject, MControllerTypeEnum controllerTypeEnum);

    /**
     * 根据元数据对象查询直属及下级所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject);
    /**
     * 根据元数据对象查询直属及下级所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject, MObjectEntity belongMObject);
    /**
     * 根据元数据对象和深度查询直属及下级所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject, Integer deep);
    /**
     * 根据id列表，查询元数据列表
     * @param ids
     * @return
     */
    public List<MirrorPropertyEntity> findByIds(List<String> ids);


    /**
     * 查询检索条件类型的元数据
     * @param metaObject 主元数据对象
     * @param maxLength 元数据的个数
     * @return
     */
    public List<MirrorPropertyEntity> getSearchConditionListProperties(MObjectEntity metaObject, Integer maxLength);

    /**
     * 查询导入需要的元数据 （包含了所有必填项）
     * @param metaObject 主元数据对象
     * @param maxLength 元数据的个数
     * @return
     */
    public List<MirrorPropertyEntity> getImportProperties(MObjectEntity metaObject, Integer maxLength);

}
