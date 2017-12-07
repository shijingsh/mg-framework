package com.mg.common.metadata.dao;

import com.mg.framework.entity.metadata.MFavoritesEntity;

import java.util.List;

/**
 * Created by liukefu on 2015/10/2.
 */
public interface MFavoritesDaoCustom {

    /**
     * 查询指定对象下，我的收藏（包含共有部分）
     * @param mainObjId
     * @return
     */
    public List<MFavoritesEntity> findAllFavorites(String mainObjId);

    /**
     * 最大的序号
     * @return
     */
    public Integer maxSort();
}
