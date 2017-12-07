package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MObjectScriptEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.entity.vo.PageTableVO;

import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/10/16.
 */
public interface MObjectScriptService {

    public MObjectScriptEntity findById(String id);

    public MObjectScriptEntity save(MObjectScriptEntity scriptEntity);

    /**
     * 元数据脚步任务列表 分页
     * @param pageTableVO
     * @return
     */
    public PageTableVO findPageList(PageTableVO pageTableVO,MObjectEntity mObjectEntity);
    /**
     * 查询所有脚本
     * @return
     */
    public List<MObjectScriptEntity> findAll();

    /**
     * 查询对象下面的脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findByBelongMObject(MObjectEntity belongMObject);

    /**
     * 查询对象下面的插入脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findInsertByBelongMObject(MObjectEntity belongMObject);

    /**
     * 查询对象下面的更新脚本
     * @param belongMObject
     * @return
     */
    public List<MObjectScriptEntity> findUpdateByBelongMObject(MObjectEntity belongMObject);

    /**
     * 执行一个task
     * @param scriptEntity
     * @return
     */
    public boolean execTask(MObjectScriptEntity scriptEntity);

    /**
     * 执行对象下面的保存task
     * @param belongMObject
     * @param map
     * @return
     */
    public boolean execObjectInsertTask(MObjectEntity belongMObject, Map<String,Object> map);

    /**
     * 执行对象下面的修改task
     * @param belongMObject
     * @param map
     * @return
     */
    public boolean execObjectUpdateTask(MObjectEntity belongMObject, MirrorPropertyEntity mPropertyEntity, Map<String,Object> map);

    /**
     * 执行对象下面的修改task
     * @param belongMObject
     * @param map
     * @return
     */
    public boolean execObjectUpdateTask(MObjectEntity belongMObject, List<MirrorPropertyEntity> propertyList, Map<String,Object> map);
}
