package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MPropertyEntity;
import com.mg.framework.entity.vo.PageTableVO;

import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/9/5.
 */
public interface MetaDataManageService {
    /**
     * 查询所有元数据对象
     * @return
     */
    public List<MObjectEntity> findAllObject();
    /**
     * 元数据对象列表 分页
     * @param map
     * @return
     */
    public PageTableVO findPageList(Map<String, Object> map);
    /**
     * 保存元数据对象
     * @param objectEntity
     * @return
     */
    public MObjectEntity saveObject(MObjectEntity objectEntity);
    /**
     * 保存元数据
     * @param propertyEntity
     * @return
     */
    public MPropertyEntity saveProperty(MPropertyEntity propertyEntity);
    /**
     * 查询所有元数据
     * @return
     */
    public List<MPropertyEntity> findAllProperty();
    /**
     * 查询所有元数据
     * @return
     */
    public List<MPropertyEntity> findAllProperty(MObjectEntity objectEntity);
    /**
     * 根据元数据ID查询元数据
     * @param mPropertyId
     * @return
     */
    public MPropertyEntity findMPropertyById(String mPropertyId);

    /**
     * 创建元数据镜像
     * @param objectEntity
     */
    public void createMirrorProperties(MObjectEntity objectEntity);

    /**
     * 根据元数据对象，重新创建表结构
     * @param objectEntity
     * @return
     */
    public boolean refreshTables(MObjectEntity objectEntity);

    /**
     * 根据实体类创建元数据
     * @param entityClazz
     */
    public boolean createMObjectFromEntityClass(Class entityClazz);

    /**
     * 生成元数据的元数据
     */
    public boolean createSelf();
}
