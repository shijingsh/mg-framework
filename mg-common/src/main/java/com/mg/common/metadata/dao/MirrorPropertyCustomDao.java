package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import com.mg.framework.entity.metadata.MTypeEnum;

import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */

public interface MirrorPropertyCustomDao {

    /**
     * 查询元数据对象下面，所以的镜像
     * 包含关联属性，结构化属性
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject);
    /**
     * 查询元数据对象下面，所以的镜像
     * 包含关联属性，结构化属性
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject, MObjectEntity belongMObject);
    /**
     * 查询元数据对象下面，所以的镜像
     * @param rootMObject
     * @param deep
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject, Integer deep);
    /**
     * 根据元数据对象和元数据路径，查询元数据列表
     * @param rootMObject
     * @param path
     * @return
     */
    public MirrorPropertyEntity findMPropertyByRootMObjectAndPath(MObjectEntity rootMObject, String path);
    /**
     * 根据主对象，查询默认的检索条件
     * 集合是按优先级排序的
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findSearchConditionByRootMObject(MObjectEntity rootMObject);

    public List<MirrorPropertyEntity> findByNameAndIsEnable(String name, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndIsEnable(MObjectEntity belongMObject, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndIsPrimaryKeyAndIsEnable(MObjectEntity belongMObject, boolean isPrimaryKey, boolean isEnable);

    public List<MirrorPropertyEntity> findByTypeEnumAndIsEnable(MTypeEnum typeEnum, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject,
                                                                            String name, boolean isEnable);
    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject,
                                                                            String[] names);
    public List<MirrorPropertyEntity> findAllByBelongMObjectAndNameAndIsEnable(MObjectEntity belongMObject, String name, boolean isEnable);
    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity belongMObjectAndName, String name, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndFieldNameAndIsEnable(MObjectEntity belongMObject, String fieldName, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndFieldNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName, boolean isEnable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndIsEnableAndIsNullable(MObjectEntity belongMObject, boolean isEnable, boolean isNullable);

    public List<MirrorPropertyEntity> findByBelongMObjectAndControllerTypeAndIsEnable(MObjectEntity belongMObject, MControllerTypeEnum controllerTypeEnum, boolean isEnable);

    public List<MirrorPropertyEntity> findByParentProperty(MirrorPropertyEntity parentProperty);

    public MirrorPropertyEntity findOne(String mPropertyId);

    /**
     * 根据id列表，查询元数据列表
     * @param ids
     * @return
     */
    public List<MirrorPropertyEntity> findByIds(List<String> ids);
}