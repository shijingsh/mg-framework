package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MObjectScriptDao;
import com.mg.common.metadata.groovy.MetaDataScriptEngineUtil;
import com.mg.framework.entity.metadata.*;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.framework.entity.vo.PageTableVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元数据对象，批量更新脚本、定时任务
 * Created by liukefu on 2015/10/16.
 */
@Service
public class MObjectScriptServiceImpl implements MObjectScriptService{
    @Autowired
    MObjectScriptDao mObjectScriptDao;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataExpressService metaDataExpressionService;
    @PersistenceContext
    EntityManager entityManager;
    public MObjectScriptEntity findById(String id) {
        MObjectScriptEntity scriptEntity = mObjectScriptDao.findOne(id);

        return scriptEntity;
    }

    public MObjectScriptEntity save(MObjectScriptEntity scriptEntity) {

        return mObjectScriptDao.save(scriptEntity);
    }

    public List<MObjectScriptEntity> findList(PageTableVO pageTableVO, MObjectEntity mObjectEntity) {
        QMObjectScriptEntity objectScriptEntity = QMObjectScriptEntity.mObjectScriptEntity;

        JPAQuery query = new JPAQuery(entityManager);
        List<MObjectScriptEntity> scriptEntities = query.from(objectScriptEntity)
                .where(//objectScriptEntity.isEnable.eq(true)
                        objectScriptEntity.belongMObject.eq(mObjectEntity))
                .offset(pageTableVO.getOffset()).limit(pageTableVO.getPageSize())
                .list(objectScriptEntity);

        return scriptEntities;
    }

    public Long findCount(PageTableVO pageTableVO,MObjectEntity mObjectEntity) {
        QMObjectScriptEntity objectScriptEntity = QMObjectScriptEntity.mObjectScriptEntity;

        JPAQuery query = new JPAQuery(entityManager);
        Long totalNum = query.from(objectScriptEntity)
                .where(//objectScriptEntity.isEnable.eq(true)
                        objectScriptEntity.belongMObject.eq(mObjectEntity))
                .offset(pageTableVO.getOffset()).limit(pageTableVO.getPageSize()).count();
        return totalNum;
    }
    /**
     * 元数据脚步任务列表 分页
     * @param pageTableVO
     * @return
     */
    public PageTableVO findPageList(PageTableVO pageTableVO,MObjectEntity mObjectEntity) {

        List<MObjectScriptEntity> list = findList(pageTableVO,mObjectEntity);
        Long totalCount = findCount(pageTableVO,mObjectEntity);

        pageTableVO.setRowData(list);
        pageTableVO.setTotalCount(totalCount);
        return pageTableVO;
    }
    /**
     * 查询所有脚本
     * @return
     */
    public List<MObjectScriptEntity> findAll(){
        return mObjectScriptDao.findByIsEnable(true);
    }

    /**
     * 查询对象下面的脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findByBelongMObject(MObjectEntity belongMObject){
        return mObjectScriptDao.findByBelongMObjectAndIsEnable(belongMObject, true);
    }
    /**
     * 查询对象下面的插入脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findInsertByBelongMObject(MObjectEntity belongMObject){
        return mObjectScriptDao.findByBelongMObjectAndExecOnInsertAndIsEnable(belongMObject, true, true);
    }
    /**
     * 查询对象下面的更新脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findUpdateByBelongMObject(MObjectEntity belongMObject){
        return mObjectScriptDao.findByBelongMObjectAndExecOnUpdateAndIsEnable(belongMObject, true, true);
    }

    /**
     * 执行一个task
     * @param scriptEntity
     * @return
     */
    @Transactional
    public boolean execTask(MObjectScriptEntity scriptEntity){
        //执行周期性的任务
        if(scriptEntity!=null && scriptEntity.getPeriodicity()){
            MExpressGroupEntity expressGroupEntity = metaDataExpressionService.createBlankExpressGroup(null);

            List<Map<String,Object>> list = metaDataService.queryByMetaData(scriptEntity.getBelongMObject(), expressGroupEntity);

            for(Map<String,Object> map:list){
                Map<String,Object> param = new HashMap<>();
                param.put(MetaDataUtils.META_CURR_OBJECT,map);
                try {
                    MetaDataScriptEngineUtil.execGroovyScript(scriptEntity.getExecScript(), param);
                } catch (ScriptException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 执行一个task
     * @param scriptEntity
     * @return
     */
    @Transactional
    public boolean execTask(MObjectScriptEntity scriptEntity, Map<String,Object> map){

        if(StringUtils.isNotBlank(scriptEntity.getExecScript())){
            Map<String,Object> param = new HashMap<>();
            param.put(MetaDataUtils.META_CURR_OBJECT,map);
            try {
                MetaDataScriptEngineUtil.execGroovyScript(scriptEntity.getExecScript(),param);
            } catch (ScriptException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * 执行对象下面的保存task
     * @param belongMObject
     * @param map
     * @return
     */
    @Transactional
    public boolean execObjectInsertTask(MObjectEntity belongMObject, Map<String,Object> map){

        List<MObjectScriptEntity> list = findInsertByBelongMObject(belongMObject);
        for (MObjectScriptEntity scriptEntity:list){
            execTask(scriptEntity, map);
        }
        return true;
    }
    /**
     * 执行对象下面的修改task
     * @param belongMObject
     * @param map
     * @return
     */
    @Transactional
    public boolean execObjectUpdateTask(MObjectEntity belongMObject, MirrorPropertyEntity mPropertyEntity, Map<String,Object> map){

        List<MObjectScriptEntity> list = findUpdateByBelongMObject(belongMObject);
        for (MObjectScriptEntity scriptEntity:list){
            String execOnPropertiesUpdate = scriptEntity.getExecOnPropertiesUpdate();
            if(StringUtils.isBlank(execOnPropertiesUpdate)){
                execTask(scriptEntity, map);
            }else if(matchedProperty(mPropertyEntity,execOnPropertiesUpdate)){
                execTask(scriptEntity, map);
            }
        }
        return true;
    }
    /**
     * 执行对象下面的修改task
     * @param belongMObject
     * @param map
     * @return
     */
    @Transactional
    public boolean execObjectUpdateTask(MObjectEntity belongMObject, List<MirrorPropertyEntity> propertyList, Map<String,Object> map){

        List<MObjectScriptEntity> list = findUpdateByBelongMObject(belongMObject);
        for (MObjectScriptEntity scriptEntity:list){
            String execOnPropertiesUpdate = scriptEntity.getExecOnPropertiesUpdate();
            if(StringUtils.isBlank(execOnPropertiesUpdate)){
                execTask(scriptEntity, map);
            }else{
                //找到触发条件
                for(MirrorPropertyEntity propertyEntity:propertyList){
                    if(matchedProperty(propertyEntity,execOnPropertiesUpdate)){
                        execTask(scriptEntity, map);
                        //每个脚本只执行一次
                        break;
                    }
                }
            }
        }
        return true;
    }
    /**
     * 检查
     * @param mPropertyEntity
     * @param execOnPropertiesUpdate
     * @return
     */
    private boolean matchedProperty(MirrorPropertyEntity mPropertyEntity, String execOnPropertiesUpdate){
        boolean matched = false;
        String arr[] = execOnPropertiesUpdate.split(";");
        for(String property:arr){
            if(StringUtils.equals(property,mPropertyEntity.getName())){
                return true;
            }
        }
         return matched;
    }
}
