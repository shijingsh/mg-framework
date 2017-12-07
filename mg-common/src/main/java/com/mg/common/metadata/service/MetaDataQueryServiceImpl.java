package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MObjectDao;
import com.mg.common.metadata.dao.MObjectDaoCustom;
import com.mg.common.metadata.util.MirrorPropertyComparator;
import com.mg.common.metadata.dao.MirrorPropertyCustomDao;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.metadata.MControllerTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liukefu on 2015/8/20.
 */
@Service
public class MetaDataQueryServiceImpl implements MetaDataQueryService {
    @Autowired
    private MObjectDao mObjectDao;
    @Autowired
    private MirrorPropertyCustomDao mPropertyDao;
    @Autowired
    private MObjectDaoCustom mObjectDaoCustom;
    /**
     * 获取员工的元数据对象
     * @return
     */
    public MObjectEntity findEmployeeMObject(){
        List<String> names = new ArrayList<>();
        names.add("人员");
        names.add("员工");
        names.add("雇员");
        names.add("职员");
        names.add("雇工");
        names.add("职工");
        return mObjectDaoCustom.findEmployeeMObject(names);
    }
    /**
     * 根据ID获取元数据对象
     * @param id
     * @return
     */
    public MObjectEntity findMObjectById(String id){

        MObjectEntity objectEntity =  mObjectDao.findOne(id);

        return objectEntity;
    }
    /**
     * 根据名称获取元数据对象
     * @param name
     * @return
     */
    public MObjectEntity findMObjectByName(String name){

        return mObjectDaoCustom.findMObjectByName(name);
    }
    /**
     * 查询所有元数据对象
     * @return
     */
    public List<MObjectEntity> findAll(){
        List<MObjectEntity> list =  mObjectDao.findByIsEnable(true);

        return list;
    }
    /**
     * 根据模块查询下面维护的对象
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findMObjectByModuleName(String moduleName){
        List<MObjectEntity> list =  mObjectDao.findByModuleNameAndIsEnable(moduleName, true);

        return list;
    }
    /**
     * 根据模块查询下面维护的对象
     * @param moduleName
     * @return
     */
    public List<MObjectEntity> findMObjectManagedByModuleName(String moduleName){
        List<MObjectEntity> list ;
        if(StringUtils.isNotBlank(moduleName)){
            list =  mObjectDao.findByModuleNameAndIsManageAndIsEnable(moduleName, true, true);
        }else{
            list =  mObjectDao.findByIsEnable(true);
        }
        return list;
    }

    /**
     * 根据元数据ID查询元数据
     * @param mPropertyId
     * @return
     */
    public MirrorPropertyEntity findMPropertyById(String mPropertyId){
        return mPropertyDao.findOne(mPropertyId);
    }

    /**
     * 根据名称查找元数据
     * 名称直接匹配的优先，否则返回第二名称匹配
     * @param list
     * @param name
     * @return
     */
    private MirrorPropertyEntity findMPropertyByName(List<MirrorPropertyEntity> list, String name){

        for (MirrorPropertyEntity propertyEntity:list){
            if(StringUtils.equals(name,propertyEntity.getName())){
                return propertyEntity;
            }
        }
        for (MirrorPropertyEntity propertyEntity:list){
            if(StringUtils.equals(name,propertyEntity.getSecondName())){
                return propertyEntity;
            }
        }

        return null;
    }
    /**
     * 根据元数据对象、元数据名称查询元数据
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByName(MObjectEntity belongMObject, String name){
        //先在直属属性中找是否有对应元数据
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndNameAndIsEnable(belongMObject,name, true);

        return findMPropertyByName(list,name);
    }

    /**
     * 根据元数据对象，查询元数据列表
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndName(MObjectEntity belongMObject, String name){
        if(belongMObject==null || StringUtils.isBlank(name)){
            return null;
        }
        List<MirrorPropertyEntity> list = mPropertyDao.findByBelongMObjectAndNameAndIsEnable(belongMObject, name, true);

        return findMPropertyByName(list,name);
    }

    /**
     * 根据元数据对象，查询元数据
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyAllByBelongMObjectAndName(MObjectEntity belongMObject, String name){
        if(belongMObject==null || StringUtils.isBlank(name)){
            return null;
        }
        List<MirrorPropertyEntity> list = mPropertyDao.findAllByBelongMObjectAndNameAndIsEnable(belongMObject, name, true);

        return findMPropertyByName(list,name);
    }
    /**
     * 根据元数据根对象和上级对象和名称，查询元数据
     * @param belongMObject
     * @param name
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndName(MObjectEntity rootMObject, MObjectEntity belongMObject, String name){
        if(rootMObject==null|| belongMObject==null || StringUtils.isBlank(name)){
            return null;
        }
        List<MirrorPropertyEntity> list = mPropertyDao.findByBelongMObjectAndNameAndIsEnable(rootMObject, belongMObject, name, true);

        return findMPropertyByName(list,name);
    }

    /**
     * 根据元数据根对象和上级对象和名称列表，查询元数据列表
     * （注意：不支持第二名称查找）
     * @param rootMObject
     * @param belongMObject
     * @param names
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectAndNames(MObjectEntity rootMObject, MObjectEntity belongMObject, String[] names){
        if(rootMObject==null|| belongMObject==null){
            return null;
        }
        List<MirrorPropertyEntity> list = mPropertyDao.findByBelongMObjectAndNameAndIsEnable(rootMObject, belongMObject, names);


        return list;
    }
    /**
     * 根据元数据对象和元数据路径，查询元数据
     * @param rootMObject
     * @param path
     * @return
     */
    public MirrorPropertyEntity findMPropertyByRootMObjectAndPath(MObjectEntity rootMObject, String path){
        return mPropertyDao.findMPropertyByRootMObjectAndPath(rootMObject,path);
    }

    /**
     * 根据元数据对象和字段，查询元数据
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldName(MObjectEntity belongMObject, String fieldName){

        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndFieldNameAndIsEnable(belongMObject, fieldName, true);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }

        return null;
    }
    /**
     * 根据元数据主对象，对象和字段，查询元数据
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldName(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName){

        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndFieldNameAndIsEnable(rootMObject, belongMObject, fieldName, true);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }

        return null;
    }
    /**
     * 根据元数据主对象、对象、字段和层级，查询元数据
     * @param belongMObject
     * @param fieldName
     * @return
     */
    public MirrorPropertyEntity findMPropertyByBelongMObjectAndFieldNameAndDeep(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName, Integer deep){

        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndFieldNameAndIsEnable(rootMObject, belongMObject, fieldName, true);
        if(list!=null&&list.size()>0){
            for(MirrorPropertyEntity propertyEntity:list){
                if(propertyEntity.getDeep()==deep){
                    return propertyEntity;
                }
            }
        }

        return null;
    }
    /**
     * 查找元数据对象的 PrimaryKey
     * @param belongMObject
     * @return
     */
    public MirrorPropertyEntity findPrimaryKeyMPropertyByBelongMObject(MObjectEntity belongMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndIsPrimaryKeyAndIsEnable(belongMObject, true, true);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据元数据主对象查询直属及下级所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByRootMObject(rootMObject);

        return list;
    }
    /**
     * 根据元数据主对象和上级对象查询直属的所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject, MObjectEntity belongMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByRootMObject(rootMObject, belongMObject);

        return list;
    }
    /**
     * 根据元数据主对象和层级，查询所有元数据
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByRootMObject(MObjectEntity rootMObject, Integer deep){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByRootMObject(rootMObject, deep);

        return list;
    }
    /**
     * 根据元数据对象查询元数据
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObject(MObjectEntity belongMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndIsEnable(belongMObject, true);

        return list;
    }

    /**
     * 根据元数据对象查询可编辑的元数据
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectEditable(MObjectEntity belongMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndIsEnable(belongMObject, true);
        List<MirrorPropertyEntity> listVisible = new ArrayList<>();
        for(MirrorPropertyEntity property:list){
            if(!property.getIsReadOnly()){
                listVisible.add(property);
            }
        }
        return listVisible;
    }
    /**
     * 根据元数据对象查询普通类型的元数据
     * 排除了对象类型的属性
     * @param belongMObject
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyNormalByBelongMObject(MObjectEntity belongMObject){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndIsEnable(belongMObject, true);
        List<MirrorPropertyEntity> listNormal = new ArrayList<>();
        int maxLength = 10;
        int length = 0;
        for(MirrorPropertyEntity propertyEntity:list){
            if( propertyEntity.getControllerType() != MControllerTypeEnum.subType
                    && length<maxLength
                    ){
                String fieldName = propertyEntity.getFieldName();
                if(MetaDataUtils.isSystemFields(fieldName) ){
                   continue;
                }
                listNormal.add(propertyEntity);
                length ++;
            }
        }

        return listNormal;
    }


    /**
     * 根据元数据对象查询指定类型的元数据
     * @param belongMObject
     * @param controllerTypeEnum
     * @return
     */
    public List<MirrorPropertyEntity> findMPropertyByBelongMObjectAndControllerType(MObjectEntity belongMObject, MControllerTypeEnum controllerTypeEnum){
        List<MirrorPropertyEntity> list =  mPropertyDao.findByBelongMObjectAndControllerTypeAndIsEnable(belongMObject, controllerTypeEnum, true);

        return list;
    }

    /**
     * 根据id列表，查询元数据列表
     * @param ids
     * @return
     */
    public List<MirrorPropertyEntity> findByIds(List<String> ids){
        return mPropertyDao.findByIds(ids);
    }

    /**
     * 查询检索条件类型的元数据
     * @param metaObject 主元数据对象
     * @param maxLength 元数据的个数
     * @return
     */
    public List<MirrorPropertyEntity> getSearchConditionListProperties(MObjectEntity metaObject, Integer maxLength){

        List<MirrorPropertyEntity> list = mPropertyDao.findSearchConditionByRootMObject(metaObject);
        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        int length = 0;
        if(maxLength==-1){
            maxLength = 10000;
        }
        for(MirrorPropertyEntity mirrorPropertyEntity:list){
            if(!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getControllerType() != MControllerTypeEnum.subType
                    &&length<maxLength){
                length ++;
                rtList.add(mirrorPropertyEntity);
            }
        }
        return rtList;
    }

    /**
     * 查询导入需要的元数据 （包含了所有必填项）
     * @param metaObject 主元数据对象
     * @param maxLength 元数据的个数
     * @return
     */
    public List<MirrorPropertyEntity> getImportProperties(MObjectEntity metaObject, Integer maxLength){

        List<MirrorPropertyEntity> list = findMPropertyByBelongMObject(metaObject);
        List<MirrorPropertyEntity> rtList = new ArrayList<>();
        List<MirrorPropertyEntity> leftList = new ArrayList<>();
        int length = 0;
        for(MirrorPropertyEntity mirrorPropertyEntity:list){
            //先找必填的字段
            if(!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && !mirrorPropertyEntity.getIsNullable()
                    && mirrorPropertyEntity.getControllerType() != MControllerTypeEnum.subType
                    ){
                length ++;
                rtList.add(mirrorPropertyEntity);
            }else{
                leftList.add(mirrorPropertyEntity);
            }
        }
        for(MirrorPropertyEntity mirrorPropertyEntity:leftList){
            if(!MetaDataUtils.isSystemFields(mirrorPropertyEntity.getFieldName())
                    && mirrorPropertyEntity.getControllerType() != MControllerTypeEnum.subType
                    &&length<maxLength){
                length ++;
                rtList.add(mirrorPropertyEntity);
            }
        }

        Collections.sort(rtList,new MirrorPropertyComparator());
        return rtList;
    }
}
