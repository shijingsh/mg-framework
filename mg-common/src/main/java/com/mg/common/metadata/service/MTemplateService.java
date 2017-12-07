package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;

import java.util.List;

/**
 * Created by liukefu on 2015/9/11.
 */
public interface MTemplateService {

    /**
     * 根据id查找模板
     * @param id
     * @return
     */
    public MTemplateEntity findTemplateById(String id);

    /**
     * 获取元数据对象的模板
     * 规则:
     *  不存在自定义模板，返回系统模板
     *  存在自定义模板，返回自定义模板的第一个
     * @param belongMObject
     * @param templateType
     * @return
     */
    public MTemplateEntity getTemplate(MObjectEntity belongMObject, MTemplateTypeEnum templateType);

    /**
     * 获取元数据对象的模板
     * 规则:
     *  不存在自定义模板，返回系统模板
     *  存在自定义模板，返回自定义模板的第一个
     * @param belongMObject
     * @param templateType
     * @param index  模板的索引
     * @return
     */
    public MTemplateEntity getTemplate(MObjectEntity belongMObject, MTemplateTypeEnum templateType, Integer index);

    /**
     * 获取元数据对象的模板
     * 规则:
     *  不存在自定义模板，返回系统模板
     *  存在自定义模板，返回自定义模板的第一个
     * @param belongMObject
     * @param templateType
     * @return
     */
    public List<MTemplateEntity> getTemplates(MObjectEntity belongMObject, MTemplateTypeEnum templateType);
    /**
     * 根据元数据对象，查询所有模板列表
     * 获取的模板是排序的
     * @param belongMObject
     * @return
     */
    public List<MTemplateEntity> findTemplateAll(MObjectEntity belongMObject);
    /**
     * 根据元数据对象，和模板类型，查询模板列表
     * @param belongMObject
     * @param templateType
     * @return
     */
    public List<MTemplateEntity> findByBelongMObjectAndTemplateType(MObjectEntity belongMObject,
                                                                    MTemplateTypeEnum templateType);
    /**
     * 保存模板
     * @param templateEntity
     * @return
     */
    public MTemplateEntity saveTemplate(MTemplateEntity templateEntity);
}
