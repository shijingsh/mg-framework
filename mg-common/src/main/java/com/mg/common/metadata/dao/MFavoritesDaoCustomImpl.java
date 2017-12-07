package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MFavoritesEntity;
import com.mg.framework.entity.metadata.QMFavoritesEntity;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mg.framework.utils.UserHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
@Component
public class MFavoritesDaoCustomImpl implements MFavoritesDaoCustom{
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 查询指定对象下，我的收藏（包含共有部分）
     * @param mainObjId
     * @return
     */
    public List<MFavoritesEntity> findAllFavorites(String mainObjId){

        JPAQuery query = new JPAQuery(entityManager);
        QMFavoritesEntity favorites = new QMFavoritesEntity("mpro");

        String userId = UserHolder.getLoginUserId();
        List<MFavoritesEntity> list = query.from(favorites)
                .where(favorites.belongObject.id.eq(mainObjId)
                        .and(favorites.userId.eq(userId).or(favorites.isPublic.eq(true))))
                .orderBy(favorites.sort.asc())
                .list(favorites);

        return list;
    }

    /**
     * 最大的序号
     * @return
     */
    public Integer maxSort(){

        String userId = UserHolder.getLoginUserId();

        javax.persistence.Query query = entityManager.createQuery("select max(u.sort) from MFavoritesEntity u where (u.userId = ?1 or u.isPublic = ?2 )");
        query.setParameter(1,userId);
        query.setParameter(2,true);
        Object result = query.getSingleResult();

        return  (Integer)result;
    }
}
