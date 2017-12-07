package com.mg.common.metadata.service;

import com.mg.common.metadata.dao.MTemplateDao;
import com.mg.common.metadata.dao.MTemplateDaoCustom;
import com.mg.common.metadata.freeMarker.defaulTemplate.DefaultTemplateCreator;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liukefu on 2015/9/11.
 */
@Service
public class MTemplateServiceImpl implements  MTemplateService {
    @Autowired
    MTemplateDao templateDao;
    @Autowired
    MTemplateDaoCustom mTemplateDaoCustom;
    @Autowired
    DefaultTemplateCreator defaultTemplateCreator;
    /**
     * 根据id查找模板
     * @param id
     * @return
     */
    public MTemplateEntity findTemplateById(String id){
        return templateDao.findOne(id);
    }

    /**
     * 获取元数据对象的模板
     * 规则:
     *  不存在自定义模板，返回系统模板
     *  存在自定义模板，返回自定义模板的第一个
     * @param belongMObject
     * @param templateType
     * @return
     */
    public MTemplateEntity getTemplate(MObjectEntity belongMObject, MTemplateTypeEnum templateType){
        //判断是否存在自定义的模板，有则优先返回
        List<MTemplateEntity> list = findByBelongMObjectAndTemplateType(belongMObject,templateType);
        if(list.size()>0){
            return list.get(0);
        }
        //系统模板
        list = mTemplateDaoCustom.findCustomTemplates(belongMObject, templateType, true);
        for(MTemplateEntity template:list){
            if(template.getTemplateType() == templateType){
                return template;
            }
        }
        return null;
    }

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
    public MTemplateEntity getTemplate(MObjectEntity belongMObject, MTemplateTypeEnum templateType, Integer index){
        //判断是否存在自定义的模板，有则优先返回
        List<MTemplateEntity> list = findByBelongMObjectAndTemplateType(belongMObject,templateType);
        if(list.size()>0){
            if(index>0 && index < list.size()){
               return list.get(index);
            }
            return list.get(0);
        }
        //系统模板
        list = mTemplateDaoCustom.findCustomTemplates(belongMObject, templateType, true);
        for(MTemplateEntity template:list){
            if(template.getTemplateType() == templateType){
                return template;
            }
        }
        return null;
    }

    /**
     * 获取元数据对象的模板
     * 规则:
     *  不存在自定义模板，返回系统模板
     *  存在自定义模板，返回自定义模板的第一个
     * @param belongMObject
     * @param templateType
     * @return
     */
    public List<MTemplateEntity> getTemplates(MObjectEntity belongMObject, MTemplateTypeEnum templateType){
        //判断是否存在自定义的模板，有则优先返回
        List<MTemplateEntity> list = findByBelongMObjectAndTemplateType(belongMObject,templateType);
        if(list.size()>0){
            return list;
        }
        //系统模板
        list = mTemplateDaoCustom.findCustomTemplates(belongMObject, templateType, true);
        for(MTemplateEntity template:list){
            if(template.getTemplateType() == templateType){
                List<MTemplateEntity> tmpList =new ArrayList<>();
                tmpList.add(template);
                return tmpList;
            }
        }
        return new ArrayList<>();
    }
    /**
     * 根据元数据对象，和模板类型，查询模板列表
     * @param belongMObject
     * @param templateType
     * @return
     */
    public List<MTemplateEntity> findByBelongMObjectAndTemplateType(MObjectEntity belongMObject,
                                                                    MTemplateTypeEnum templateType){
        return mTemplateDaoCustom.findCustomTemplates(belongMObject, templateType, false);
    }

    /**
     * 根据元数据对象，查询所有模板列表
     * 获取的模板是排序的
     * @param belongMObject
     * @return
     */
    public List<MTemplateEntity> findTemplateAll(MObjectEntity belongMObject){
        return mTemplateDaoCustom.findTemplateAll(belongMObject);
    }
    /**
     * 保存模板
     * @param templateEntity
     * @return
     */
    public MTemplateEntity saveTemplate(MTemplateEntity templateEntity){
        MObjectEntity mObjectEntity = templateEntity.getBelongMObject();
        //设置排序字段
        if(!templateEntity.getIsSystem() && templateEntity.getSort() == 0){
            int maxSort =   templateDao.maxSort(mObjectEntity,templateEntity.getTemplateType());
            templateEntity.setSort(maxSort+1);
        }
        //保存
        templateDao.save(templateEntity);
        //根据母模板生成模板
        if(templateEntity.getIsSystem()){
            //系统生成模板，只有一个
            String template =  defaultTemplateCreator.createTemple(mObjectEntity,templateEntity.getTemplateSource(),templateEntity.getTemplateType());
            templateEntity.setTemplate(template);
            templateDao.save(templateEntity);
        }else{
            //自定义模板，允许有多个。多个同类型的模板，共享元数据
            List<MTemplateEntity> templates =  defaultTemplateCreator.createTemple(mObjectEntity, templateEntity.getTemplateType());
            for(MTemplateEntity template:templates){
                templateDao.save(template);
            }
        }

        return templateEntity;
    }

}
