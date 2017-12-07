package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.*;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by liukefu on 2015/8/28.
 */
@Component
public class MirrorPropertyCustomDaoImpl implements MirrorPropertyCustomDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 查询元数据对象下面，所以的镜像
     * 包含关联属性，结构化属性
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.rootMObject.eq(rootMObject))
                .orderBy(mirrorProperty.deep.asc())
                .list(mirrorProperty);

        return list;
    }

    /**
     * 查询元数据对象下面，所以的镜像
     * 包含关联属性，结构化属性
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject, MObjectEntity belongMObject) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.rootMObject.eq(rootMObject)
                .and(mirrorProperty.belongMObject.eq(belongMObject))
                )
                .orderBy(mirrorProperty.deep.asc())
                .list(mirrorProperty);

        return list;
    }
    /**
     * 查询元数据对象下面，所以的镜像
     * @param rootMObject
     * @param deep
     * @return
     */
    public List<MirrorPropertyEntity> findByRootMObject(MObjectEntity rootMObject, Integer deep) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(
                        mirrorProperty.rootMObject.eq(rootMObject)
                                .and(mirrorProperty.deep.eq(deep))
                )
                .orderBy(mirrorProperty.deep.asc())
                .list(mirrorProperty);

        return list;
    }

    /**
     * 根据元数据对象和元数据路径，查询元数据列表
     * @param rootMObject
     * @param path
     * @return
     */
    public MirrorPropertyEntity findMPropertyByRootMObjectAndPath(MObjectEntity rootMObject, String path){
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(
                        mirrorProperty.rootMObject.eq(rootMObject)
                                .and(mirrorProperty.propertyPath.eq(path))
                )
                .list(mirrorProperty);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /**
     * 根据主对象，查询默认的检索条件
     * 集合是按优先级排序的
     * @param rootMObject
     * @return
     */
    public List<MirrorPropertyEntity> findSearchConditionByRootMObject(MObjectEntity rootMObject) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.rootMObject.eq(rootMObject))
                .orderBy(mirrorProperty.isSearchCondition.desc()).orderBy(mirrorProperty.deep.asc()).orderBy(mirrorProperty.sort.asc())
                .list(mirrorProperty);

        return list;
    }
    @Override
    public List<MirrorPropertyEntity> findByNameAndIsEnable(String name, boolean isEnable) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.name.eq(name))

                .list(mirrorProperty);

        return list;
    }

    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndIsEnable(MObjectEntity belongMObject, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.belongMObject.eq(belongMObject)
                        .and(mirrorProperty.rootMObject.eq(belongMObject))
                        .and(mirrorProperty.deep.eq(0))
                       )

                .list(mirrorProperty);

        return list;
    }

    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndIsPrimaryKeyAndIsEnable(MObjectEntity belongMObject, boolean isPrimaryKey, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.belongMObject.eq(belongMObject)
                                .and(mirrorProperty.rootMObject.eq(belongMObject))
                                .and(mirrorProperty.metaProperty.isEnable.eq(isEnable))
                                .and(mirrorProperty.metaProperty.isPrimaryKey.eq(isPrimaryKey))
                )

                .list(mirrorProperty);

        return list;
    }

    @Override
    public List<MirrorPropertyEntity> findByTypeEnumAndIsEnable(MTypeEnum typeEnum, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.metaProperty.typeEnum.eq(typeEnum)
                                .and(mirrorProperty.metaProperty.isEnable.eq(isEnable))
                )

                .list(mirrorProperty);

        return list;
    }

    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject,
                                                                            String name, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(
                        mirrorProperty.rootMObject.eq(rootMObject)
                                .and(mirrorProperty.belongMObject.eq(belongMObject))
                                .and(mirrorProperty.name.eq(name).or(mirrorProperty.secondName.eq(name)))
                )
                .orderBy(mirrorProperty.deep.asc())
                .list(mirrorProperty);

        return list;
    }

    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject,
                                                                            String[] names) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(
                        mirrorProperty.rootMObject.eq(rootMObject)
                                .and(mirrorProperty.belongMObject.eq(belongMObject))
                                .and(mirrorProperty.name.in(names))
                )

                .list(mirrorProperty);

        return list;
    }
    public List<MirrorPropertyEntity> findAllByBelongMObjectAndNameAndIsEnable(MObjectEntity belongMObject, String name, boolean isEnable) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(
                        mirrorProperty.rootMObject.eq(belongMObject)
                                .and(mirrorProperty.name.eq(name).or(mirrorProperty.secondName.eq(name)))
                ).orderBy(mirrorProperty.deep.asc())

                .list(mirrorProperty);

        return list;
    }

    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndNameAndIsEnable(MObjectEntity belongMObject, String name, boolean isEnable) {


        return findByBelongMObjectAndNameAndIsEnable(belongMObject,belongMObject,name,isEnable);
    }

    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndFieldNameAndIsEnable(MObjectEntity belongMObject, String fieldName, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.belongMObject.eq(belongMObject)
                                .and(mirrorProperty.fieldName.eq(fieldName))
                )

                .list(mirrorProperty);

        return list;
    }

    public List<MirrorPropertyEntity> findByBelongMObjectAndFieldNameAndIsEnable(MObjectEntity rootMObject, MObjectEntity belongMObject, String fieldName, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.rootMObject.eq(rootMObject)
                        .and(mirrorProperty.belongMObject.eq(belongMObject))
                                .and(mirrorProperty.fieldName.eq(fieldName))
                )

                .list(mirrorProperty);

        return list;
    }
    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndIsEnableAndIsNullable(MObjectEntity belongMObject, boolean isEnable, boolean isNullable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.belongMObject.eq(belongMObject)
                                .and(mirrorProperty.metaProperty.isEnable.eq(isEnable))
                                .and(mirrorProperty.metaProperty.isNullable.eq(isNullable))
                )

                .list(mirrorProperty);

        return list;
    }

    @Override
    public List<MirrorPropertyEntity> findByBelongMObjectAndControllerTypeAndIsEnable(MObjectEntity belongMObject, MControllerTypeEnum controllerTypeEnum, boolean isEnable) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.rootMObject.eq(belongMObject)
                        .and(mirrorProperty.belongMObject.eq(belongMObject))
                                .and(mirrorProperty.controllerType.eq(controllerTypeEnum))
                )

                .list(mirrorProperty);

        return list;
    }

    public List<MirrorPropertyEntity> findByParentProperty(MirrorPropertyEntity parentProperty) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.parentProperty.eq(parentProperty))
                .orderBy(mirrorProperty.deep.asc())
                .list(mirrorProperty);

        return list;
    }

    @Override
    public MirrorPropertyEntity findOne(String mPropertyId) {
        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.id.eq(mPropertyId)
                )
                .list(mirrorProperty);

        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据id列表，查询元数据列表
     * @param ids
     * @return
     */
    public List<MirrorPropertyEntity> findByIds(List<String> ids) {

        JPAQuery query = new JPAQuery(entityManager);
        QMirrorPropertyEntity mirrorProperty = new QMirrorPropertyEntity("mpro");

        List<MirrorPropertyEntity> list = query.from(mirrorProperty)
                .where(mirrorProperty.id.in(ids)
                       )

                .list(mirrorProperty);

        return list;
    }
}
