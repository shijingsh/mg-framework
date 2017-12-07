package com.mg.common.metadata.service;

import com.mg.framework.entity.metadata.MFavoritesEntity;

import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
public interface MFavoritesService {
    List<MFavoritesEntity> findAllFavorites(String mainObjId);

    MFavoritesEntity findById(String id);

    MFavoritesEntity saveFavorites(MFavoritesEntity favoritesEntity, String objId);

    int deleteById(String id);
}
