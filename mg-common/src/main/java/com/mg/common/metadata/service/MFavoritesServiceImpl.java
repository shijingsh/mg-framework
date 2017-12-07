package com.mg.common.metadata.service;

import com.alibaba.fastjson.JSONObject;
import com.mg.common.metadata.dao.MFavoritesDao;
import com.mg.common.metadata.dao.MFavoritesDaoCustom;
import com.mg.common.utils.LazyLoadUtil;
import com.mg.common.metadata.dao.MExpressGroupDao;
import com.mg.framework.entity.metadata.*;
import com.mg.framework.utils.StatusEnum;
import com.mg.framework.utils.UserHolder;
import com.mg.groovy.util.CloneFilter;
import com.mg.groovy.util.HRMSBeanClone;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
@Service
public class MFavoritesServiceImpl implements MFavoritesService{
    @Autowired
    MFavoritesDao mFavoritesDao;
    @Autowired
    MFavoritesDaoCustom mFavoritesDaoCustom;
    @Autowired
    MExpressGroupDao mExpressGroupDao;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Override
    public List<MFavoritesEntity> findAllFavorites(String mainObjId) {

        return mFavoritesDaoCustom.findAllFavorites(mainObjId);
    }

    @Transactional
    public MFavoritesEntity findById(String id) {
        MFavoritesEntity favoritesEntity = mFavoritesDao.findOne(id);

        if(favoritesEntity.getExpressGroup()!=null ){
            LazyLoadUtil.fullLoad(favoritesEntity.getExpressGroup());
        }
        return favoritesEntity;
    }

    @Transactional
    public MFavoritesEntity saveFavorites(MFavoritesEntity favoritesEntity, String objId) {
        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        if(StringUtils.isNotBlank(favoritesEntity.getId())){
            MFavoritesEntity db = mFavoritesDao.findOne(favoritesEntity.getId());
            mFavoritesDao.delete(db);
        }
        if(favoritesEntity.getExpressGroup()!=null){
            List<CloneFilter> filterList = new ArrayList<>();
            filterList.add(new CloneFilter(MExpressGroupEntity.class,"id"));
            filterList.add(new CloneFilter(MExpressionEntity.class,"id"));
            filterList.add(new CloneFilter(MObjectEntity.class,"templates"));
            try {
                MExpressGroupEntity expressGroup =  favoritesEntity.getExpressGroup();
                MExpressGroupEntity expressGroupEntity = (MExpressGroupEntity) HRMSBeanClone.deepClone(expressGroup, filterList);

                favoritesEntity.setExpressGroup(expressGroupEntity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            MExpressionEntity expressionEntity = favoritesEntity.getExpressGroup().getMatched();
            setParentExpression(expressionEntity,expressionEntity.getExpressions());

            if(favoritesEntity.getExpressGroup().getExtendData()!=null){
                List<JSONObject> jsonList = (List<JSONObject>)favoritesEntity.getExpressGroup().getExtendData();
                List<String> properties = new ArrayList<>();
                for(JSONObject jsonObject:jsonList){
                    MirrorPropertyEntity propertyEntity = JSONObject.toJavaObject(jsonObject,MirrorPropertyEntity.class);
                    properties.add(propertyEntity.getId());
                }
                favoritesEntity.setProperties(StringUtils.join(properties,";"));
            }
        }
        favoritesEntity.setBelongObject(mObjectEntity);
        if(favoritesEntity.getExpressGroup()!=null){
            favoritesEntity.getExpressGroup().setMetaObject(mObjectEntity);
        }
        favoritesEntity.setUserId(UserHolder.getLoginUserId());
        Integer maxSort = mFavoritesDaoCustom.maxSort();
        if(maxSort==null){
            maxSort = 0;
        }
        favoritesEntity.setSort(maxSort +1 );
        return mFavoritesDao.save(favoritesEntity);
    }

    private void setParentExpression(MExpressionEntity expressionEntity, List<MExpressionEntity> list){

        if(list!= null && list.size()>0){
            for(MExpressionEntity child:list){
                child.setParentExpression(expressionEntity);
                setParentExpression(child,child.getExpressions());
            }
        }
    }
    @Override
    public int deleteById(String id) {
        MFavoritesEntity favoritesEntity = mFavoritesDao.findOne(id);
        favoritesEntity.setStatus(StatusEnum.STATUS_INVALID);
        mFavoritesDao.save(favoritesEntity);
        return 0;
    }
}
