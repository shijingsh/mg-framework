package com.mg.common.user.service;

import com.mg.common.entity.MenuEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 菜单
 * Created by liukefu on 2016/3/14.
 */
public interface MenuService {

    List<MenuEntity> findALL();

    List<MenuEntity> findMenuALL();

    List<MenuEntity> findResourceALL();

    List<MenuEntity> findMyMenu();

    List<MenuEntity> findMyResource();

    List<MenuEntity> findMyUrls();

    Map<String,Object> findAllUrls();

    void setMenuChecked(String roleId,List<MenuEntity> list);

    void save(MenuEntity menuEntity);

    MenuEntity get(String menuId);

    List<MenuEntity> refreshResource(HttpServletRequest req);
}
