package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.MFavoritesService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MFavoritesEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据收藏查询
 * @author liukefu
 */
@Controller
@RequestMapping(value = "/favorites",produces = "application/json; charset=UTF-8")
public class MFavoritesController {
    @Autowired
    MFavoritesService mFavoritesService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    /**
     * 我的收藏的列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/list")
    public String list(String mainObjId) {

        List<MFavoritesEntity> list = mFavoritesService.findAllFavorites(mainObjId);
        return JsonResponse.success(list, null);
    }

    /**
     *  根据ID获取
     * @param id   请求
     * @return          templateEntity
     */
    @ResponseBody
    @RequestMapping("/get")
    public String getFavorites(String  id) {

        MFavoritesEntity favoritesEntity = mFavoritesService.findById(id);
        return JsonResponse.success(favoritesEntity, null);
    }
    /**
     * 保存
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/post")
    public String postTemplate(HttpServletRequest req,String objId) {
        String jsonString = WebUtil.getJsonBody(req);

        MFavoritesEntity favoritesEntity = JSON.parseObject(jsonString, MFavoritesEntity.class);


        mFavoritesService.saveFavorites(favoritesEntity,objId);
        return JsonResponse.success(null, null);
    }
}
