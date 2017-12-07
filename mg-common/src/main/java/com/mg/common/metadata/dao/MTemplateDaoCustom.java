package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;

import java.util.List;

/**
 * Created by liukefu on 2015/9/29.
 */
public interface MTemplateDaoCustom {

    public List<MTemplateEntity> findCustomTemplates(MObjectEntity belongMObject,
                                                     MTemplateTypeEnum templateType, Boolean isSystem);

    public List<MTemplateEntity> findTemplateAll(MObjectEntity belongMObject);
}
