package com.mg.common.metadata.service;

import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 生成任职履历、变更记录等。
 * Created by liukefu on 2015/10/19.
 */
@Service
public class MHistoryServiceImpl implements MHistoryService {
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    private MetaDataQueryService metaDataManageService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    private MetaDataExpressService metaDataExpressionService;

    public boolean createHistory(String mObjectName,String propertyName,String historyPropertyName,Map<String,Object> param){

        return createHistory(mObjectName,propertyName,historyPropertyName,null,param);
    }

    public boolean createHistory(String mObjectName,String propertyName,String historyPropertyName,String startDatePropertyName,Map<String,Object> param){
        MObjectEntity mObjectEntity = metaDataManageService.findMObjectByName(mObjectName);
        if(mObjectEntity!=null){
            MirrorPropertyEntity mirrorPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, propertyName);
            MirrorPropertyEntity historyPropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, historyPropertyName);
            if(historyPropertyEntity==null){
                return false;
            }
            if(mirrorPropertyEntity==null){
                return false;
            }
            MirrorPropertyEntity startDatePropertyEntity = null;
            if(StringUtils.isNotBlank(startDatePropertyName)){
                startDatePropertyEntity = metaDataManageService.findMPropertyByName(mObjectEntity, startDatePropertyName);
            }
            MirrorPropertyEntity pkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
            String objPKValue = (String)param.get(pkPropertyEntity.getPropertyPath());
            //变更的值
            Object changeValue = param.get(mirrorPropertyEntity.getPropertyPath());

            //结构化对象
            MPropertyEntity mappedByProperty = historyPropertyEntity.getMetaProperty().getMappedByProperty();
            MObjectEntity joinedMObjet = historyPropertyEntity.getMetaProperty().getMetaObject();
            MirrorPropertyEntity foreignkeyProperty = metaDataManageService.findMPropertyByBelongMObjectAndFieldName(joinedMObjet,mappedByProperty.getFieldName());

            //生成简单表达式
            MExpressionEntity expression =  metaDataExpressionService.createSimpleEqExpress(foreignkeyProperty, objPKValue);
            MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);

            List<Map<String,Object>> historyList = metaDataService.queryByMetaData(historyPropertyEntity.getMetaProperty().getMetaObject(), expressGroup);
            Map<String,Object> lastRecord = new HashMap<>();
            boolean hasChange = true;
            if(historyList!=null && historyList.size()>0){
                lastRecord = historyList.get(historyList.size()-1);
                //获取最后一次变更的值
                String lastValue = String.valueOf(lastRecord.get(mirrorPropertyEntity.getPropertyPath()));
                if(StringUtils.equals(String.valueOf(changeValue), lastValue)){
                    hasChange = false;
                }
            }else{
                if(changeValue==null || StringUtils.isBlank(String.valueOf(changeValue))){
                    hasChange = false;
                }
            }
            if(hasChange){
                List<MirrorPropertyEntity> mPropertyEntityList = metaDataQueryService.findMPropertyByRootMObject(mObjectEntity);
                Map<String,Object> map = metaDataService.queryById(mObjectEntity.getId(), objPKValue,mPropertyEntityList);
                //生成历史记录
                createHistory(historyPropertyEntity,map,lastRecord,startDatePropertyEntity);
            }
        }

        return true;
    }
    public boolean createHistory(MirrorPropertyEntity historyPropertyEntity,
                                 Map<String,Object> map,Map<String,Object> lastRecord,
                                 MirrorPropertyEntity startDatePropertyEntity){
        MObjectEntity mObjectEntity =  historyPropertyEntity.getMetaProperty().getMetaObject();
        //修改上一条历史记录结束时间为当前时间
        if(lastRecord!= null){
            MirrorPropertyEntity pkPropertyEntity = metaDataManageService.findPrimaryKeyMPropertyByBelongMObject(mObjectEntity);
            MirrorPropertyEntity endDatePropertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(mObjectEntity, mObjectEntity, MetaDataUtils.META_HISTORY_END_DATE);
            String objPKValue = (String)lastRecord.get(pkPropertyEntity.getPropertyPath());
            lastRecord.put(endDatePropertyEntity.getPropertyPath(),new Date());
            metaDataService.updateByMetaData(endDatePropertyEntity.getId(), objPKValue, lastRecord);
        }

        //新增一条历史记录
        List<MirrorPropertyEntity> list = metaDataManageService.findMPropertyByBelongMObject(mObjectEntity);
        Map<String,Object> valueMap = new HashMap<>();
        for (MirrorPropertyEntity propertyEntity:list){
            if(MetaDataUtils.isSystemFields(propertyEntity.getFieldName())){
                //跳过系统字段
                continue;
            }
            if(StringUtils.equals(MetaDataUtils.META_HISTORY_END_DATE,propertyEntity.getFieldName())){
                //跳过结束时间字段，新增一条记录，结束时间必定为空
                valueMap.put(MetaDataUtils.META_HISTORY_END_DATE,null);
                continue;
            }
            if(StringUtils.equals(MetaDataUtils.META_HISTORY_START_DATE,propertyEntity.getFieldName())){
                Object date = null;
                if(startDatePropertyEntity!=null && lastRecord== null){
                    date = map.get(startDatePropertyEntity.getPropertyPath());
                }
                if(date==null){
                    valueMap.put(MetaDataUtils.META_HISTORY_START_DATE,new Date());
                }else{
                    valueMap.put(MetaDataUtils.META_HISTORY_START_DATE,date);
                }
            }else if(StringUtils.startsWithIgnoreCase(propertyEntity.getFieldName(),MetaDataUtils.META_HISTORY_PRE)){
                String [] arr = propertyEntity.getFieldName().split(MetaDataUtils.META_HISTORY_PRE);
                String fieldPath = arr[1];
                valueMap.put(propertyEntity.getPropertyPath(),lastRecord.get(fieldPath));
            }else{
                valueMap.put(propertyEntity.getPropertyPath(),map.get(propertyEntity.getPropertyPath()));
            }
        }
        //设置外键
        valueMap.put(MetaDataUtils.META_HISTORY_ID,map.get(MetaDataUtils.META_FIELD_ID));
        metaDataService.saveByMetaData(mObjectEntity.getId(),valueMap);
        return true;
    }
}
