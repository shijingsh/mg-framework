package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MTemplateEntity;
import com.mg.framework.entity.metadata.MTemplateTypeEnum;
import com.mg.framework.entity.metadata.QMTemplateEntity;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by liukefu on 2015/9/29.
 */
@Component
public class MTemplateDaoCustomImpl implements MTemplateDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List<MTemplateEntity> findCustomTemplates(MObjectEntity belongMObject,
                                                     MTemplateTypeEnum templateType, Boolean isSystem) {
        QMTemplateEntity obj = QMTemplateEntity.mTemplateEntity;

        JPAQuery query = new JPAQuery(entityManager);
        List<MTemplateEntity> templateList = query.from(obj)
                .where(obj.isSystem.eq(isSystem)
                                .and(obj.belongMObject.eq(belongMObject))
                                .and(obj.templateType.eq(templateType))
                )
                .orderBy(obj.isSystem.desc())
                .orderBy(obj.templateType.asc())
                .orderBy(obj.sort.asc())
                .list(obj);

        return templateList;
    }

    public List<MTemplateEntity> findTemplateAll(MObjectEntity belongMObject) {
        QMTemplateEntity obj = QMTemplateEntity.mTemplateEntity;

        JPAQuery query = new JPAQuery(entityManager);
        List<MTemplateEntity> templateList = query.from(obj)
                .where(obj.belongMObject.eq(belongMObject)
                )
                .orderBy(obj.isSystem.desc())
                .orderBy(obj.templateType.asc())
                .orderBy(obj.sort.asc())
                .list(obj);

        return templateList;
    }
}
