package com.mg.common.metadata.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mg.common.entity.UserRuleEntity;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.metadata.util.MirrorPropertyDeepComparator;
import com.mg.common.metadata.vo.MTable;
import com.mg.common.user.service.UserRuleService;
import com.mg.common.utils.DateUtil;
import com.mg.common.metadata.vo.TableRelation;
import com.mg.framework.exception.ServiceException;
import com.mg.framework.entity.metadata.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 元数据 服务实现类
 */
@Service
public class MetaDataServiceImpl implements MetaDataService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MetaDataQueryService metaDataManageService;
    @Autowired
    private MetaDataExpressService metaDataExpressionService;
    @Autowired
    private MetaDataCoreService metaDataCoreService;
    @Autowired
    private MEnumService mEnumService;
    @Autowired
    private UserRuleService userRuleService;

    /**
     * 保存元数据对象
     *
     * @param mObjectId 主元数据对象ID
     * @return
     */
    public String saveByMetaData(String mObjectId, Map dataMap) throws ServiceException {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //保存主元数据
        String mainObjectId = saveObject(mObjectEntity, dataMap);
        //保存元数据分级对象
/*        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObjectAndType(mObjectEntity, MControllerTypeEnum.object);
        for(MirrorPropertyEntity mPropertyEntity:mPropertyEntityList){
            saveRelaObject(mainObjectId,mPropertyEntity, dataMap);
        }*/
        //保存结构化对象
        List<MirrorPropertyEntity> mSubPropertyEntityList = metaDataManageService.findMPropertyByBelongMObjectAndControllerType(mObjectEntity, MControllerTypeEnum.subType);
        for (MirrorPropertyEntity mPropertyEntity : mSubPropertyEntityList) {
            saveSubObject(mainObjectId, mPropertyEntity, dataMap);
        }
        //返回主元数据对象的ID
        return mainObjectId;
    }

    /**
     * 保存元数据主对象数据
     *
     * @param mObjectEntity 元数据对象
     * @return 对象ID
     * @throws ServiceException
     */
    public String saveObject(MObjectEntity mObjectEntity, Map dataMap) throws ServiceException {
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObjectEditable(mObjectEntity);
        List<MirrorPropertyEntity> normalList = new ArrayList<>();
        for (MirrorPropertyEntity propertyEntity : mPropertyEntityList) {
            if (propertyEntity.getControllerType() != MControllerTypeEnum.subType) {
                normalList.add(propertyEntity);
            }
        }
        _readMetaData(normalList, dataMap);
        //保存
        return _save(mObjectEntity, normalList);
    }

    /**
     * 保存元数据主对象数据
     *
     * @param mObjectEntity 元数据对象
     * @return 对象ID
     * @throws ServiceException
     */
    public String saveObject(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList, Map dataMap) throws ServiceException {


        _readMetaData(mPropertyEntityList, dataMap);
        //保存
        return _save(mObjectEntity, mPropertyEntityList);
    }

    /**
     * 保存元数据分级对象数据
     *
     * @param mPropertyEntity 元数据
     * @return 对象ID
     * @throws ServiceException
     */
    public String saveRelaObject(String mainObjectId, MirrorPropertyEntity mPropertyEntity, Map dataMap) throws ServiceException {
        if (mPropertyEntity == null || dataMap.get(mPropertyEntity.getPropertyPath()) == null) {
            return null;
        }
        MObjectEntity mRelaObjectEntity = mPropertyEntity.getMetaProperty().getMetaObject();
        //保存
        String relationObjectId = saveObject(mRelaObjectEntity, dataMap);
        //更新与主对象的关联关系
        updateByMetaData(mPropertyEntity, relationObjectId, dataMap);
        return relationObjectId;
    }

    /**
     * 保存结构化对象
     *
     * @param mainObjectId    主对象ID
     * @param mPropertyEntity 结构化字段对应的元数据
     * @param dataMap         主数据
     * @return
     * @throws ServiceException
     */
    public String saveSubObject(String mainObjectId, MirrorPropertyEntity mPropertyEntity, Map dataMap) throws ServiceException {
        //结构化对象
        MObjectEntity mObjectEntity = mPropertyEntity.getMetaProperty().getMetaObject();
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObject(mObjectEntity);
        //获取结构化表单数据
        JSONArray array = (JSONArray) dataMap.get(mPropertyEntity.getPropertyPath());
        if (array != null) {
            JSONObject[] jsonObjects = array.toArray(new JSONObject[array.size()]);
            for (int i = 0; i < jsonObjects.length; i++) {
                JSONObject jsonObject = jsonObjects[i];
                Map map = JSONObject.toJavaObject(jsonObject, Map.class);
                //设置元数据的值
                _readMetaData(mPropertyEntityList, map);
                //设置与主对象的外键
                _setSubObjectForeignkey(mainObjectId, mPropertyEntity, mPropertyEntityList);
                //保存
                _save(mObjectEntity, mPropertyEntityList);
            }
        }
        return null;
    }

    /**
     * 修改单个元数据字段的值
     *
     * @param mPropertyEntity
     * @param objPKValue
     * @param dataMap
     * @return
     */
    public int updateByMetaData(MirrorPropertyEntity mPropertyEntity, String objPKValue, Map dataMap) {
        if (mPropertyEntity != null) {
            //从表单中读取对应值
            _readMetaData(mPropertyEntity, dataMap);
            //设置主键
            MirrorPropertyEntity mPkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mPropertyEntity.getRootMObject());
            mPkPropertyEntity.setFieldValue(objPKValue);
            //修改
            List<MirrorPropertyEntity> list = new ArrayList<>();
            list.add(mPropertyEntity);
            list.add(mPkPropertyEntity);
            return _update(mPropertyEntity.getBelongMObject(), list);
        }
        return 0;
    }

    /**
     * 修改单个元数据字段的值
     *
     * @param mPropertyId 元数据id
     * @param objPKValue  数据的pk
     * @param dataMap     数据域
     * @return
     */
    public int updateByMetaData(String mPropertyId, String objPKValue, Map dataMap) {
        MirrorPropertyEntity mPropertyEntity = metaDataManageService.findMPropertyById(mPropertyId);

        return updateByMetaData(mPropertyEntity, objPKValue, dataMap);
    }

    /**
     * 修改多个元数据字段的值
     * update 最多只能更新一张表
     *
     * @param mObjectId
     * @param dataMap
     * @return
     */
    public List<MirrorPropertyEntity> updateMutiByMetaData(String mObjectId, Map dataMap) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);

        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObject(mObjectEntity);
        //被修改的元数据列表
        List<MirrorPropertyEntity> mPropertModifiedList = new ArrayList<>();
        for (MirrorPropertyEntity mPropertyEntity : mPropertyEntityList) {
            //如果MAP中有元数据的ID，就是被修改的
            if (dataMap.keySet().contains(mPropertyEntity.getPropertyPath())) {
                mPropertModifiedList.add(mPropertyEntity);
            }
        }

        _readMetaData(mPropertModifiedList, dataMap);
        //保存
        _update(mObjectEntity, mPropertModifiedList);

        return mPropertModifiedList;
    }

    /**
     * 删除一条记录
     *
     * @param mObjectId  元数据id
     * @param objPKValue 数据的pk
     * @return
     */
    public int deleteById(String mObjectId, String objPKValue) {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        MirrorPropertyEntity propertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        propertyEntity.setFieldValue(objPKValue);

        return metaDataCoreService._delete(mObjectEntity, propertyEntity);
    }
    //---------------------------------------查询类方法-------------------------------------------------------------------------------

    /**
     * 根据元数据对象名称和数据ID，查询匹配数据
     *
     * @param mObjectId 对象名称
     * @param id        primaryKey 值
     * @return Map<元数据别名,value>
     */
    public Map<String, Object> queryById(String mObjectId, String id) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //主键
        MirrorPropertyEntity pkMProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        //查询的元数据集合，即查询结果返回那些值
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObject(mObjectEntity);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(pkMProperty, id);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new HashMap<>();
    }

    /**
     * 根据对象名称，查询对象
     *
     * @param mObjectId
     * @param name
     * @return
     */
    public Map<String, Object> queryByName(String mObjectId, String name) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //主键
        MirrorPropertyEntity nameMProperty = metaDataManageService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity, mObjectEntity, MetaDataUtils.META_FIELD_NAME);
        //查询的元数据集合，即查询结果返回那些值
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByBelongMObject(mObjectEntity);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(nameMProperty, name);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new HashMap<>();
    }

    /**
     * 根据对象名称，查询对象ID
     *
     * @param mObjectId
     * @param name
     * @return
     */
    public String queryIdByName(String mObjectId, String name) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //主键
        MirrorPropertyEntity pkMProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        //name
        MirrorPropertyEntity nameMProperty = metaDataManageService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity, mObjectEntity, MetaDataUtils.META_FIELD_NAME);
        //查询的元数据集合，即查询结果返回那些值
        List<MirrorPropertyEntity> mPropertyEntityList = new ArrayList<>();
        mPropertyEntityList.add(pkMProperty);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(nameMProperty, name);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get(pkMProperty.getPropertyPath());
        }
        return null;
    }

    /**
     * 根据对象唯一标识，查询对象ID
     *
     * @param mObjectId
     * @param identifierValue
     * @return
     */
    public String queryIdByIdentifier(String mObjectId, String identifierValue) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //主键
        MirrorPropertyEntity pkMProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        //对象的唯一标识
        String identifier = MetaDataUtils.getIdentifier(mObjectEntity);
        MirrorPropertyEntity nameMProperty = metaDataManageService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity, mObjectEntity, identifier);
        //查询的元数据集合，即查询结果返回那些值
        List<MirrorPropertyEntity> mPropertyEntityList = new ArrayList<>();
        mPropertyEntityList.add(pkMProperty);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(nameMProperty, identifierValue);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get(pkMProperty.getPropertyPath());
        }
        return null;
    }

    /**
     * 根据元数据对象名称和数据ID，查询匹配数据
     *
     * @param mObjectId
     * @param id
     * @return
     */
    public Map<String, Object> queryAllPropertiesById(String mObjectId, String id) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyByRootMObject(mObjectEntity);
        //主键
        MirrorPropertyEntity pkMProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(pkMProperty, id);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        Map<String, Object> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            map = list.get(0);
            String mainObjectId = (String) map.get(MetaDataUtils.META_FIELD_ID);
            //结构化属性
            for (MirrorPropertyEntity propertyEntity : mPropertyEntityList) {
                if (propertyEntity.getControllerType() == MControllerTypeEnum.subType) {
                    List<Map<String, Object>> subList = queryStructsByMetaData(propertyEntity, mainObjectId);

                    map.put(propertyEntity.getPropertyPath(), subList);
                }
            }
        }

        return map;
    }

    /**
     * 根据元数据对象名称和数据ID，查询匹配数据
     *
     * @param mObjectId
     * @param id
     * @param mPropertyEntityList
     * @return
     */
    public Map<String, Object> queryById(String mObjectId, String id, List<MirrorPropertyEntity> mPropertyEntityList) {
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectById(mObjectId);
        //主键
        MirrorPropertyEntity pkMProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(pkMProperty, id);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new HashMap<>();
    }

    /**
     * 查询单个元数据的值
     *
     * @param mObjectName  对象名称
     * @param propertyName 元数据名称
     * @param objId        对象 pk
     * @return
     */
    public Object queryMetaData(String mObjectName, String propertyName, String objId) {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectByName(mObjectName);
        if (mObjectEntity != null) {
            MirrorPropertyEntity mirrorPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, propertyName);
            List<MirrorPropertyEntity> list = new ArrayList<>();
            list.add(mirrorPropertyEntity);
            if (mirrorPropertyEntity != null) {
                Map<String, Object> map = queryById(mObjectName, objId, list);

                return map.get(mirrorPropertyEntity.getPropertyPath());
            }
        }

        return null;
    }

    /**
     * 从map中取单个元数据的值
     *
     * @param propertyName
     * @param param
     * @return
     */
    public Object queryMetaData(String mObjectName, String propertyName, Map<String, Object> param) {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectByName(mObjectName);
        if (mObjectEntity != null) {
            MirrorPropertyEntity mirrorPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, propertyName);
            if (mirrorPropertyEntity != null) {
                return param.get(mirrorPropertyEntity.getPropertyPath());
            } else {
                logger.debug("cannot find metadata:{} in queryMetaData", propertyName);
            }
        }

        return null;
    }

    /**
     * 更新单个元数据的值
     *
     * @param mObjectName
     * @param propertyName
     * @param param
     * @return
     */
    public int updateMetaData(String mObjectName, String propertyName, Map<String, Object> param) {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectByName(mObjectName);
        if (mObjectEntity != null) {
            MirrorPropertyEntity mirrorPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, propertyName);
            MirrorPropertyEntity pkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
            String objPKValue = (String) param.get(pkPropertyEntity.getPropertyPath());
            return updateByMetaData(mirrorPropertyEntity, objPKValue, param);
        }

        return 0;
    }

    /**
     * 更新单个元数据的值
     *
     * @param mObjectName
     * @param propertyName
     * @param value
     * @param param
     * @return
     */
    public int updateMetaData(String mObjectName, String propertyName, Object value, Map<String, Object> param) {

        MObjectEntity mObjectEntity = metaDataManageService.findMObjectByName(mObjectName);
        if (mObjectEntity != null) {
            MirrorPropertyEntity mirrorPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, propertyName);
            MirrorPropertyEntity pkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
            String objPKValue = (String) param.get(pkPropertyEntity.getPropertyPath());
            //修改的值，放入当前数据map
            param.put(mirrorPropertyEntity.getPropertyPath(), value);
            return updateByMetaData(mirrorPropertyEntity, objPKValue, param);
        }

        return 0;
    }

    /**
     * 根据条件组，查询对象的名称列表
     *
     * @param expressGroupEntity
     * @return
     */
    public List<String> queryNames(MExpressGroupEntity expressGroupEntity) {
        MObjectEntity mObjectEntity = metaDataManageService.findEmployeeMObject();

        if (mObjectEntity != null) {
            return queryNames(mObjectEntity, expressGroupEntity);
        }

        return new ArrayList<>();
    }

    /**
     * 根据条件组，查询对象的名称列表
     *
     * @param mObjectEntity
     * @param expressGroupEntity
     * @return
     */
    public List<String> queryNames(MObjectEntity mObjectEntity, MExpressGroupEntity expressGroupEntity) {
        //对象的唯一标识
        UserRuleEntity userRuleEntity = userRuleService.get();
        MirrorPropertyEntity nameMProperty = metaDataManageService.findMPropertyByBelongMObjectAndName(mObjectEntity, userRuleEntity.getLoginName());
        List<MirrorPropertyEntity> mPropertyEntityList = new ArrayList<>();
        mPropertyEntityList.add(nameMProperty);

        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroupEntity);
        List<String> listNames = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String name = String.valueOf(map.get(nameMProperty.getPropertyPath()));
            listNames.add(name);
        }
        //根据ids 查询
        if (StringUtils.isNotBlank(expressGroupEntity.getObjectIds())) {
            MirrorPropertyEntity mProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
            //根节点 匹配全部
            MExpressionEntity machedAll = new MExpressionEntity(true, true);

            String ids[] = expressGroupEntity.getObjectIds().split(";");
            for (String id : ids) {
                if (StringUtils.isNotBlank(id)) {
                    //二级节点 匹配任一
                    MExpressionEntity machedOne = metaDataExpressionService.createqExpress(mProperty, id);
                    machedAll.addExpressions(machedOne);
                }
            }
            if (machedAll.getExpressions().size() > 0) {
                MExpressGroupEntity expressGroup = new MExpressGroupEntity(machedAll);
                list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroup);
                for (Map<String, Object> map : list) {
                    String name = String.valueOf(map.get(nameMProperty.getPropertyPath()));
                    listNames.add(name);
                }
            }
        }
        return listNames;
    }


    /**
     * 根据元数据对象和条件组，查询匹配数据的ID集合
     *
     * @param mObjectEntity
     * @param expressGroupEntity
     * @return
     */
    public List<String> queryIds(MObjectEntity mObjectEntity, MExpressGroupEntity expressGroupEntity) {

        MirrorPropertyEntity mProperty = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
        List<MirrorPropertyEntity> mPropertyEntityList = new ArrayList<>();
        mPropertyEntityList.add(mProperty);

        List<Map<String, Object>> list = queryByMetaData(mObjectEntity, mPropertyEntityList, expressGroupEntity);
        List<String> listIds = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String name = String.valueOf(map.get(mProperty.getPropertyPath()));
            listIds.add(name);
        }
        return listIds;
    }

    /**
     * 根据元数据对象和条件组，查询匹配数据集合
     *
     * @param mObject
     * @param expressGroupEntity
     * @return
     */
    public List<Map<String, Object>> queryByMetaData(MObjectEntity mObject, MExpressGroupEntity expressGroupEntity) {
        //查询的元数据集合，即查询结果返回那些值
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyNormalByBelongMObject(mObject);

        return queryByMetaData(mObject, mPropertyEntityList, expressGroupEntity);
    }

    /**
     * 根据元数据和主对象ID,查询结构化属性列表
     *
     * @param propertyEntity
     * @param mainObjectId
     * @return
     */
    public List<Map<String, Object>> queryStructsByMetaData(MirrorPropertyEntity propertyEntity, String mainObjectId) {
        MObjectEntity mObject = propertyEntity.getMetaProperty().getMetaObject();

        //在关联关系中寻找与主对象之间的外键字段
        MPropertyEntity foreignkeyProperty = propertyEntity.getMetaProperty().getMappedByProperty();
        //查询的元数据集合，即查询结果返回那些值
        MirrorPropertyEntity propertyForeignkeyEntity = null;
        List<MirrorPropertyEntity> mPropertyEntityList = metaDataManageService.findMPropertyNormalByBelongMObject(mObject);
        for (MirrorPropertyEntity mPropertyEntity : mPropertyEntityList) {
            if (StringUtils.equals(mPropertyEntity.getFieldName(), foreignkeyProperty.getFieldName())) {
                mPropertyEntity.setFieldValue(mainObjectId);
                propertyForeignkeyEntity = mPropertyEntity;
                break;
            }
        }
        //生成简单表达式
        MExpressionEntity expression = metaDataExpressionService.createSimpleEqExpress(propertyForeignkeyEntity, mainObjectId);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);

        return queryByMetaData(mObject, mPropertyEntityList, expressGroup);
    }

    /**
     * 添加属性本身，及上级所以属性
     *
     * @param allMProperties
     * @param property
     */
    private void addProperties(Map<String, Object> propertyRecord, List<MirrorPropertyEntity> allMProperties, MirrorPropertyEntity property) {
        if (property.getParentProperty() != null) {
            addProperties(propertyRecord, allMProperties, property.getParentProperty());
        }
        addProperty(propertyRecord, allMProperties, property);
    }

    /**
     * 过滤重复
     *
     * @param propertyRecord
     * @param allMProperties
     * @param showProperty
     */
    public void addProperty(Map<String, Object> propertyRecord, List<MirrorPropertyEntity> allMProperties, MirrorPropertyEntity showProperty) {
        if (propertyRecord.get(showProperty.getPropertyPath()) == null) {
            allMProperties.add(showProperty);
            propertyRecord.put(showProperty.getPropertyPath(), "");
        }
    }

    /**
     * 设置表之间的连接关系
     *
     * @param mObject
     * @param showMProperties
     * @param expressGroupEntity
     * @param joinedObjs
     * @param joinedMapping
     */
    private List<MirrorPropertyEntity> setJoinedRelation(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties,
                                                         MExpressGroupEntity expressGroupEntity,
                                                         List<TableRelation> joinedObjs,
                                                         Map<String, MTable> joinedMapping
    ) {
        Map<String, Object> propertyRecord = new HashMap<>();
        Map<String, Object> propertySelectRecord = new HashMap<>();
        List<MirrorPropertyEntity> allMProperties = new ArrayList<>();
        List<MirrorPropertyEntity> selectMProperties = new ArrayList<>();

        for (MirrorPropertyEntity showProperty : showMProperties) {
            addProperties(propertyRecord, allMProperties, showProperty);
            addProperty(propertySelectRecord, selectMProperties, showProperty);
            if (showProperty.getControllerType() == MControllerTypeEnum.object
                //|| showProperty.getControllerType() == MControllerTypeEnum.subType
                    ) {
                //对象类型的元数据，查询对象的“name” 字段
                MirrorPropertyEntity nameProperty = metaDataManageService.findMPropertyByBelongMObjectAndFieldNameAndDeep(mObject, showProperty.getMetaProperty().getMetaObject(),
                        MetaDataUtils.META_FIELD_NAME, showProperty.getDeep() + 1);
                if (nameProperty != null) {
                    addProperty(propertyRecord, allMProperties, nameProperty);
                    addProperty(propertySelectRecord, selectMProperties, nameProperty);
                }
            }
        }
        if (!expressGroupEntity.getIsDistinct()) {
            //不是去重复的查询，添加ID
            MirrorPropertyEntity mPkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObject);
            addProperty(propertyRecord, allMProperties, mPkPropertyEntity);
            addProperty(propertySelectRecord, selectMProperties, mPkPropertyEntity);
        }
        //添加条件表达式中的元数据
        if (expressGroupEntity.getMatched() != null) {
            List<MExpressionEntity> machedList = expressGroupEntity.getMatched().getExpressions();
            if (machedList != null) {
                for (MExpressionEntity expressionEntity : machedList) {
                    List<MExpressionEntity> list = expressionEntity.getExpressions();
                    for (MExpressionEntity express : list) {
                        MirrorPropertyEntity property = metaDataManageService.findMPropertyById(express.getProperty().getId());
                        property.setFieldValue(express.getProperty().getFieldValue());
                        express.setProperty(property);
                        addProperties(propertyRecord, allMProperties, property);
                    }
                }
            }
        }
        Collections.sort(allMProperties, new MirrorPropertyDeepComparator());
        /**
         * 记录表结构是否被join 过
         */
        Map<String, String> recordMapping = new HashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        for (MirrorPropertyEntity mPropertyEntity : allMProperties) {
            //直属对象
            MObjectEntity belongMObject = mPropertyEntity.getBelongMObject();
            int subIndex = mPropertyEntity.getPropertyPath().indexOf(MetaDataUtils.SQL_UNDERLINE);
            //忽略主元数据对象下面的元数据
            if (StringUtils.equals(belongMObject.getId(), mObject.getId())
                    && (subIndex == -1 || belongMObject.getIsHistory())       //不是历史表，因为历史表的字段里可能存在 SQL_UNDERLINE
                    ) {
                String aliasName = belongMObject.getTableName() + index;
                MTable mainTable = new MTable(belongMObject.getTableName(), aliasName);
                if(joinedObjs.size()==0){
                    //查询的主表，关联表为空
                    TableRelation tableRelation = new TableRelation(mainTable, null, null);
                    joinedObjs.add(tableRelation);
                }
                joinedMapping.put(mPropertyEntity.getId(), mainTable);
                continue;
            }
            //下级属性
            _joinedObjects(mObject, joinedObjs, joinedMapping, recordMapping, mPropertyEntity, index);
        }

        return selectMProperties;
    }

    /**
     * 根据元数据对象和条件组，查询匹配数据集合，并设置返回的数据范围
     *
     * @param mObject
     * @param showMProperties    要求按照deep字段asc有序，用来简化表之间的连接关系
     * @param expressGroupEntity
     * @return
     */
    public List<Map<String, Object>> queryByMetaData(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties, MExpressGroupEntity expressGroupEntity) {

        /**
         * 查询需要连接的对象
         */
        List<TableRelation> joinedObjs = new ArrayList<>();
        /**
         * 属性所属对象，以保存属性对应的别名
         */
        Map<String, MTable> joinedMapping = new HashMap<>();
        /**
         * 设置连接关系
         */
        List<MirrorPropertyEntity> queryMProperties = setJoinedRelation(mObject, showMProperties, expressGroupEntity, joinedObjs, joinedMapping);

        return _query(mObject, queryMProperties, joinedObjs, joinedMapping, expressGroupEntity);
    }

    /**
     * 根据元数据对象和条件组，查询匹配数据集合，并设置返回的数据范围
     *
     * @param mObject
     * @param showMProperties    要求按照deep字段asc有序，用来简化表之间的连接关系
     * @param expressGroupEntity
     * @return
     */
    public Integer queryCountByMetaData(MObjectEntity mObject, List<MirrorPropertyEntity> showMProperties, MExpressGroupEntity expressGroupEntity) {
        /**
         * 查询需要连接的对象
         */
        List<TableRelation> joinedObjs = new ArrayList<>();
        /**
         * 属性所属对象，以保存属性对应的别名
         */
        Map<String, MTable> joinedMapping = new HashMap<>();
        /**
         * 设置连接关系
         */
        setJoinedRelation(mObject, showMProperties, expressGroupEntity, joinedObjs, joinedMapping);

        return _queryCount(mObject, showMProperties, joinedObjs, joinedMapping, expressGroupEntity);
    }

    //-------------------------------------------------------------------------------------------------------------------

    /**
     * 查询元数据对应的值
     * 如果是枚举：返回枚举ID
     * 如果是对象：返回对象ID
     * 其他：直接返回
     *
     * @param property
     * @param value
     * @return
     */
    public Object getMetaDataValue(MirrorPropertyEntity property, Object value) {
        if (value == null || property == null) {
            return null;
        }
        if (property.getControllerType() == MControllerTypeEnum.mEnum) {
            MEnumEntity enumEntity = mEnumService.findByName(property.getEnumName(), String.valueOf(value));
            if (enumEntity != null) {
                return enumEntity.getKey();
            }
            return null;
        } else if (property.getControllerType() == MControllerTypeEnum.bool) {
            if (StringUtils.equals("是", (String) value)) {
                return 1;
            }
            return 0;
        } else if (property.getControllerType() == MControllerTypeEnum.object) {
            return queryIdByName(property.getMetaProperty().getMetaObject().getId(), (String) value);
        }
        return value;
    }

    /**
     * 查询元数据对应的值
     * 如果是枚举：返回枚举名称
     * 如果是对象：返回对象名称
     * 其他：直接返回
     *
     * @param property
     * @param map
     * @return
     */
    public Object getMetaDataDisplayValue(MirrorPropertyEntity property, Map<String, Object> map) {
        if (map == null || property == null || map.get(property.getPropertyPath()) == null) {
            return null;
        }
        Object value = map.get(property.getPropertyPath());
        if (property.getControllerType() == MControllerTypeEnum.mEnum) {
            MEnumEntity enumEntity = mEnumService.findByKey(property.getEnumName(),String.valueOf(value));
            if (enumEntity != null) {
                return enumEntity.getName();
            }
            return null;
        } else if (property.getControllerType() == MControllerTypeEnum.bool) {
            if (StringUtils.equals("1", String.valueOf(value))) {
                return "是";
            }
            return "否";
        } else if (property.getControllerType() == MControllerTypeEnum.object) {
            value = MetaDataUtils.getName(property, map);
        }
        return value;
    }
    //-----------------------------------------核心服务方法---------------------------------------------------------------------------------------------------------------

    /**
     * 转化值到元数据对应的数据类型
     *
     * @param mPropertyEntity
     * @param value
     * @return
     */
    public Object _conversionFieldValue(MirrorPropertyEntity mPropertyEntity, String value) {
        if (value == null) return null;
        switch (mPropertyEntity.getMetaProperty().getFieldType()) {
            case TEXT:
            case VARCHAR:
                return String.valueOf(value);
            case BOOL:
                return Boolean.parseBoolean(value);
            case DATE:
                return DateUtil.convertStringToDate(value);
            case INTEGER:
                return new Integer(value);
            case LONG:
                return new Long(value);
            case DOUBLE:
                return new Double(value);
            case DECIMAL:
                return new BigDecimal(value);
            case DATETIME:
                return DateUtil.convertStringToDate(DateUtil.FORMATTER, value);
        }
        return null;
    }

    /**
     * 设置元数据对象中、元数据的值
     * 用于提取前端传过来的数据
     *
     * @param mPropertyEntityList
     * @param dataMap
     * @throws ServiceException
     */
    public void _readMetaData(List<MirrorPropertyEntity> mPropertyEntityList, Map dataMap) throws ServiceException {

        for (MirrorPropertyEntity mPropertyEntity : mPropertyEntityList) {

            _readMetaData(mPropertyEntity, dataMap);
        }
    }

    /**
     * 设置单个元数据的值
     *
     * @param mPropertyEntity
     * @param dataMap
     * @throws ServiceException
     */
    public void _readMetaData(MirrorPropertyEntity mPropertyEntity, Map dataMap) throws ServiceException {

        Object value = dataMap.get(mPropertyEntity.getPropertyPath());
        if (value == null || StringUtils.isBlank(String.valueOf(value))) {
            if (!mPropertyEntity.getMetaProperty().getIsNullable() && !mPropertyEntity.getMetaProperty().getIsPrimaryKey()) {
                // throw new ServiceException(mPropertyEntity.getName()+"不能为空！");
            }
            //设置默认值
            if (StringUtils.isNotBlank(mPropertyEntity.getMetaProperty().getFieldDefaultValue())) {
                Object objValue = _conversionFieldValue(mPropertyEntity, mPropertyEntity.getMetaProperty().getFieldDefaultValue());
                mPropertyEntity.setFieldValue(objValue);
            } else {
                mPropertyEntity.setFieldValue(null);
            }
        } else {
            if (Map.class.isAssignableFrom(value.getClass())) {
                value = ((Map) value).get(MetaDataUtils.META_FIELD_ID);
            }
            mPropertyEntity.setFieldValue(value);
        }
    }

    /**
     * 设置结构化字段与主对象直接的外键值
     *
     * @param mainObjectId        主对象ID
     * @param mPropertyEntity     元数据
     * @param mPropertyEntityList 结构化元数据列表
     */
    public void _setSubObjectForeignkey(String mainObjectId, MirrorPropertyEntity mPropertyEntity, List<MirrorPropertyEntity> mPropertyEntityList) {

        //在关联关系中寻找与主对象之间的外键字段
        MPropertyEntity foreignkeyProperty = mPropertyEntity.getMetaProperty().getMappedByProperty();

        //设置外键的值
        if (foreignkeyProperty != null) {
            for (MirrorPropertyEntity propertyEntity : mPropertyEntityList) {
                if (StringUtils.equals(propertyEntity.getFieldName(), foreignkeyProperty.getFieldName())) {
                    propertyEntity.setFieldValue(mainObjectId);
                }
            }
        }
    }

    /**
     * 计算单个关联属性，需要连接的表结构
     *
     * @param mObject
     * @param joinedObjs
     * @param joinedMapping
     * @param recordMapping
     * @param mPropertyEntity
     * @param index
     */
    public void _joinedObjects(MObjectEntity mObject,
                               List<TableRelation> joinedObjs,
                               Map<String, MTable> joinedMapping,
                               Map<String, String> recordMapping,
                               MirrorPropertyEntity mPropertyEntity, AtomicInteger index) {

        //所属对象
        MObjectEntity belongMObject = mPropertyEntity.getBelongMObject();
        //上级元数据
        MirrorPropertyEntity joinedProperty = mPropertyEntity.getParentProperty();
        if (recordMapping.get(joinedProperty.getId()) == null) {
            index.getAndIncrement();
            //joined
            MObjectEntity preObject = joinedProperty.getBelongMObject();
            MObjectEntity currMObject = joinedProperty.getMetaProperty().getMetaObject();
            String aliasName = preObject.getTableName() + (index.intValue() - 1);
            if (joinedMapping.get(joinedProperty.getId()) != null) {
                aliasName = joinedMapping.get(joinedProperty.getId()).getAliasName();
            }
            MTable mainTable = new MTable(preObject.getTableName(), aliasName);
            MTable joinedTable = new MTable(currMObject.getTableName(), currMObject.getTableName() + index.intValue());
            TableRelation tableRelation = new TableRelation(mainTable, joinedTable, joinedProperty.getMetaProperty());
            //结构化属性
            if (joinedProperty.getMetaProperty().getMappedByProperty() != null) {
                tableRelation.setMappedByProperty(joinedProperty.getMetaProperty().getMappedByProperty());
            }
            joinedObjs.add(tableRelation);
            recordMapping.put(joinedProperty.getId(), belongMObject.getTableName() + index.intValue());
        }
        String aliasName = recordMapping.get(joinedProperty.getId());
        if (recordMapping.get(joinedProperty.getId()) == null) {
            aliasName = belongMObject.getTableName() + index.intValue();
            recordMapping.put(joinedProperty.getId(), aliasName);
        }
        joinedMapping.put(mPropertyEntity.getId(), new MTable(belongMObject.getTableName(), aliasName));
    }

    /**
     * 保存元数据对象
     *
     * @param mObjectEntity       元数据对象
     * @param mPropertyEntityList 元数据列表
     * @return
     */
    public String _save(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {

        return metaDataCoreService._save(mObjectEntity, mPropertyEntityList);
    }

    /**
     * 修改单个元数据的值
     *
     * @param mPropertyEntityList
     * @return
     */
    public int _update(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> mPropertyEntityList) {

        return metaDataCoreService._update(mObjectEntity, mPropertyEntityList);
    }

    /**
     * 根据元数据、条件组，查询数据
     *
     * @param mObjectEntity      主元数据对象
     * @param showMProperties    查询返回的元数据
     * @param joinedObjs         需要连接的表
     * @param expressGroupEntity 查询条件
     * @return List<Map>
     */
    public List<Map<String, Object>> _query(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> showMProperties,
                                            List<TableRelation> joinedObjs,
                                            Map<String, MTable> joinedMapping,
                                            MExpressGroupEntity expressGroupEntity) {

        return metaDataCoreService._query(mObjectEntity, showMProperties, joinedObjs, joinedMapping, expressGroupEntity);
    }

    /**
     * 根据元数据、条件组，查询数据
     *
     * @param mObjectEntity      主元数据对象
     * @param showMProperties    查询返回的元数据
     * @param joinedObjs         需要连接的表
     * @param expressGroupEntity 查询条件
     * @return Integer
     */
    public Integer _queryCount(MObjectEntity mObjectEntity, List<MirrorPropertyEntity> showMProperties,
                               List<TableRelation> joinedObjs,
                               Map<String, MTable> joinedMapping,
                               MExpressGroupEntity expressGroupEntity) {

        return metaDataCoreService._queryCount(mObjectEntity, showMProperties, joinedObjs, joinedMapping, expressGroupEntity);
    }
}
