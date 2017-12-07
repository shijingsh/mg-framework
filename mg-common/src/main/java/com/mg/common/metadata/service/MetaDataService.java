package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * 元数据 接口
 */
public interface MetaDataService {

    /**
     * 保存元数据对象
     * @param mObjectId 主元数据对象ID
     * @return
     */
     String saveByMetaData(String mObjectId,Map dataMap) throws ServiceException;

    /**
     * 保存元数据主对象数据
     * @param mObjectEntity  元数据对象
     * @return 对象ID
     * @throws ServiceException
     */
     String saveObject(MObjectEntity mObjectEntity, Map dataMap) throws ServiceException;

    /**
     * 保存元数据主对象数据
     * @param mObjectEntity  元数据对象
     * @return 对象ID
     * @throws ServiceException
     */
    public String saveObject(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList, Map dataMap) throws ServiceException;

    /**
     * 保存结构化对象
     * @param mainObjectId  主对象ID
     * @param mPropertyEntity 结构化字段对应的元数据
     * @param dataMap 主数据
     * @return
     * @throws ServiceException
     */
     String saveSubObject(String mainObjectId, MirrorPropertyEntity mPropertyEntity, Map dataMap) throws ServiceException;

    /**
     * 修改单个元数据字段的值
     * @param mPropertyEntity
     * @param objPKValue
     * @param dataMap
     * @return
     */
     int updateByMetaData(MirrorPropertyEntity mPropertyEntity, String objPKValue, Map dataMap);

    /**
     * 修改单个元数据字段的值
     * @param mPropertyId
     * @param objPKValue
     * @param dataMap
     * @return
     */
     int updateByMetaData(String mPropertyId,String objPKValue,Map dataMap);
    /**
     * 修改多个元数据字段的值
     * update 最多只能更新一张表
     * @param mObjectId
     * @param dataMap
     * @return
     */
    List<MirrorPropertyEntity> updateMutiByMetaData(String mObjectId, Map dataMap);

    /**
     * 删除一条记录
     * @param mObjectId 元数据id
     * @param objPKValue    数据的pk
     * @return
     */
    public int deleteById(String mObjectId,String objPKValue);
    /**
     * 根据元数据对象名称和数据ID，查询匹配数据
     * @param mObjectId 对象名称
     * @param id      primaryKey 值
     * @return Map<元数据别名,value>
     */
     Map<String,Object> queryById(String mObjectId,String id);
    /**
     * 根据对象名称，查询对象
     * @param mObjectId
     * @param name
     * @return
     */
    public Map<String,Object> queryByName(String mObjectId,String name);

    /**
     *  根据对象名称，查询对象ID
     * @param mObjectId
     * @param name
     * @return
     */
    public String queryIdByName(String mObjectId,String name);

    /**
     *  根据对象唯一标识，查询对象ID
     * @param mObjectId
     * @param identifierValue
     * @return
     */
    public String queryIdByIdentifier(String mObjectId,String identifierValue);
    /**根据元数据对象名称和数据ID，查询匹配数据
     * @param mObjectId
     * @param id
     * @return
     */
    public Map<String,Object> queryAllPropertiesById(String mObjectId,String id);

    /**根据元数据对象名称和数据ID，查询匹配数据
     * @param mObjectId
     * @param id
     * @param mPropertyEntityList
     * @return
     */
    public Map<String,Object> queryById(String mObjectId,String id,List<MirrorPropertyEntity> mPropertyEntityList);

    /**
     * 从map中取单个元数据的值
     * @param propertyName
     * @param param
     * @return
     */
    public Object queryMetaData(String mObjectName,String propertyName,Map<String,Object> param);

    /**
     * 更新单个元数据的值
     * @param mObjectName
     * @param propertyName
     * @param value
     * @param param
     * @return
     */
    public int updateMetaData(String mObjectName,String propertyName,Object value,Map<String,Object> param);
    /**
     * 根据元数据对象和条件组，查询匹配数据集合
     * @param mObject
     * @param expressGroupEntity
     * @return
     */
     List<Map<String,Object>> queryByMetaData(MObjectEntity mObject, MExpressGroupEntity expressGroupEntity);

    /**
     * 根据元数据对象和条件组，查询匹配数据集合数
     * @param mObject
     * @param showMProperties  要求按照deep字段asc有序，用来简化表之间的连接关系
     * @param expressGroupEntity
     * @return
     */
    public  Integer queryCountByMetaData(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties, MExpressGroupEntity expressGroupEntity);
    /**
     * 根据条件组，查询对象的名称列表
     * @param expressGroupEntity
     * @return
     */
    public  List<String> queryNames(MExpressGroupEntity expressGroupEntity);

    /**
     * 根据元数据对象和条件组，查询匹配数据的ID集合
     *
     * @param mObjectEntity
     * @param expressGroupEntity
     * @return
     */
    public List<String> queryIds(MObjectEntity mObjectEntity, MExpressGroupEntity expressGroupEntity);
    /**
     * 根据元数据对象和条件组，查询匹配数据集合，并设置返回的数据范围
     * @param mObject
     * @param showMProperties
     * @param expressGroupEntity
     * @return
     */
     List<Map<String,Object>> queryByMetaData(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties, MExpressGroupEntity expressGroupEntity);

    /**
     * 根据元数据和主对象ID,查询结构化属性列表
     * @param propertyEntity
     * @param mainObjectId
     * @return
     */
    public  List<Map<String,Object>> queryStructsByMetaData(MirrorPropertyEntity propertyEntity, String mainObjectId);
    /**
     * 查询元数据对应的值
     * 如果是枚举：返回枚举id
     * 如果是对象：返回对象id
     * 其他：直接返回
     * @param property
     * @param value
     * @return
     */
    public Object getMetaDataValue(MirrorPropertyEntity property, Object value);

    /**
     * 查询元数据对应的值
     * 如果是枚举：返回枚举名称
     * 如果是对象：返回对象名称
     * 其他：直接返回
     * @param property
     * @param map
     * @return
     */
    public Object getMetaDataDisplayValue(MirrorPropertyEntity property, Map<String,Object> map);
}
