package com.mg.common.metadata.dao;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.entity.metadata.QMEnumEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by liukefu on 2015/9/6.
 */
@Component
public class MEnumDaoImpl implements MEnumDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 查询所有的枚举类型
     * @return
     */
    public List<MEnumEntity> findAllEnum() {

        JPAQuery query = new JPAQuery(entityManager);
        QMEnumEntity mEnumEntity = new QMEnumEntity("mpro");

        List<MEnumEntity> list = query.from(mEnumEntity)
                .groupBy(mEnumEntity.enumName)
                .list(mEnumEntity);

        return list;
    }

    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @return
     */
    public List<MEnumEntity> findByEnumName(String enumName) {

        JPAQuery query = new JPAQuery(entityManager);
        QMEnumEntity mEnumEntity = new QMEnumEntity("mpro");

        List<MEnumEntity> list = query.from(mEnumEntity)
                .where(mEnumEntity.enumName.eq(enumName))

                .list(mEnumEntity);

        return list;
    }

    /**
     * 根据枚举名称，查询枚举类型
     * @param enumName
     * @return
     */
    public MEnumEntity findByName(String enumName, String name) {

        JPAQuery query = new JPAQuery(entityManager);
        QMEnumEntity mEnumEntity = new QMEnumEntity("mpro");

        List<MEnumEntity> list = query.from(mEnumEntity)
                .where(mEnumEntity.enumName.eq(enumName).and(mEnumEntity.name.eq(name)))

                .list(mEnumEntity);

        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据枚举key，查询枚举类型
     * @param enumName
     * @return
     */
    public MEnumEntity findByKey(String enumName, String key) {

        JPAQuery query = new JPAQuery(entityManager);
        QMEnumEntity mEnumEntity = new QMEnumEntity("mpro");

        List<MEnumEntity> list = query.from(mEnumEntity)
                .where(mEnumEntity.enumName.eq(enumName).and(mEnumEntity.key.eq(key)))

                .list(mEnumEntity);

        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /**
     * 根据枚举ID，查询枚举类型
     * @param id
     * @return
     */
    public MEnumEntity findById(String id) {

        JPAQuery query = new JPAQuery(entityManager);
        QMEnumEntity mEnumEntity = new QMEnumEntity("mpro");

        List<MEnumEntity> list = query.from(mEnumEntity)
                .where(mEnumEntity.id.eq(id))

                .list(mEnumEntity);

        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

}
