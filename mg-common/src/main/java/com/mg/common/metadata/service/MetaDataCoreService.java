package com.mg.common.metadata.service;

import com.mg.common.metadata.vo.MTable;
import com.mg.common.metadata.vo.TableRelation;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MPropertyEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/8/28.
 */
public interface MetaDataCoreService {

    /**
     * 保存元数据对象
     * @param mObjectEntity 元数据对象
     * @param mPropertyEntityList 元数据列表
     * @return
     */
    public String _save(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList);

    /**
     * 修改多个元数据的值
     * @param mPropertyEntityList
     * @return
     */
    public int _update(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList);

    /**
     * 删除一条记录
     * @param pkProperty
     * @return
     */
    public int _delete(MObjectEntity mObjectEntity, MirrorPropertyEntity pkProperty);

    /**
     * 根据元数据、条件组，查询数据
     * @param mObject   主元数据对象
     * @param showMProperties 查询返回的元数据
     * @param joinedObjs      需要连接的表
     * @param expressGroupEntity 查询条件
     * @return List<Map>
     */
    public List<Map<String,Object>> _query(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                                           List<TableRelation> joinedObjs,
                                           Map<String,MTable> joinedMapping,
                                           MExpressGroupEntity expressGroupEntity);

    /**
     * 根据元数据、条件组，查询数据
     * @param mObject   主元数据对象
     * @param showMProperties 查询返回的元数据
     * @param joinedObjs      需要连接的表
     * @param expressGroupEntity 查询条件
     * @return Integer
     */
    public Integer _queryCount(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                               List<TableRelation> joinedObjs,
                               Map<String,MTable> joinedMapping,
                               MExpressGroupEntity expressGroupEntity);
    //--------------------------------------------------------根据元数据生成表结构---------------------------------------------
    /**
     * 根据元数据对象，创建表结构
     * 默认创建ID主键
     * @param mObject
     * @return
     */
    public boolean _createTabel(MObjectEntity mObject);

    /**
     * 根据元数据生成字段
     * @param mObject
     * @param mPropertyList
     * @return
     */
    public boolean _createFields(MObjectEntity mObject, List<MPropertyEntity> mPropertyList);

    /**
     * 根据元数据生成字段SQL
     * @param mProperty
     * @return
     */
    public boolean _createFields(MPropertyEntity mProperty);

    /**
     * 根据元数据更新字段
     * @param mProperty
     * @return
     */
    public boolean _updateFields(MPropertyEntity mProperty);
    /**
     * 根据元数据删除字段
     * @param mProperty
     * @return
     */
    public boolean _dropFields(MPropertyEntity mProperty);
}
