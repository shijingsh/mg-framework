package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mg.common.entity.MenuEntity;
import com.mg.common.user.service.MenuService;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 菜单管理
 * Created by liukefu on 2016/3/14.
 */
@Controller
@RequestMapping(value = "/menu",
        produces = "application/json; charset=UTF-8")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 保存菜单
     * @return
     */
    @ResponseBody
    @RequestMapping("/post")
    public String post(HttpServletRequest req) {

        String jsonString = WebUtil.getJsonBody(req);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String parentMenuId = jsonObject.getString("belong_menu_id");
        MenuEntity menuEntity = JSON.parseObject(jsonString, MenuEntity.class);
        if(StringUtils.isNotBlank(parentMenuId)){
            MenuEntity parentMenu =menuService.get(parentMenuId);
            menuEntity.setBelongMenu(parentMenu);
        }

        menuService.save(menuEntity);

        return JsonResponse.success(null, null);
    }
    /**
     * 当前用户的菜单列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/myMenu")
    public String myMenu() {

        List<MenuEntity> list = menuService.findMyMenu();

        return JsonResponse.success(list, null);
    }

    /**
     * 当前用户的url菜单列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/myResource")
    public String myResource() {

        List<MenuEntity> list = menuService.findMyResource();

        return JsonResponse.success(list, null);
    }
    /**
     * 所有的菜单
     * @return
     */
    @ResponseBody
    @RequestMapping("/all")
    public String list(String roleId) {

        List<MenuEntity> list = menuService.findMenuALL();

        if(StringUtils.isNotBlank(roleId)){
            menuService.setMenuChecked(roleId,list);
        }
        return JsonResponse.success(list, null);
    }

    /**
     * 所有的url菜单
     * @return
     */
    @ResponseBody
    @RequestMapping("/allResource")
    public String allResource(String roleId) {

        List<MenuEntity> list = menuService.findResourceALL();
        if(StringUtils.isNotBlank(roleId)){
            menuService.setMenuChecked(roleId,list);
        }
        for (MenuEntity menuEntity:list){
            String name = menuEntity.getPath();
            if(StringUtils.isNotBlank(menuEntity.getName())){
                name = "["+menuEntity.getName()+"]"+menuEntity.getPath();
            }
            menuEntity.setName(name);
        }
        return JsonResponse.success(list, null);
    }

    /**
     * 所有的url菜单
     * @return
     */
    @ResponseBody
    @RequestMapping("/refreshResource")
    public String refreshResource(String roleId,HttpServletRequest req) {

        List<MenuEntity> list = menuService.refreshResource(req);
        if(StringUtils.isNotBlank(roleId)){
            menuService.setMenuChecked(roleId,list);
        }
        for (MenuEntity menuEntity:list){
            String name = menuEntity.getPath();
            if(StringUtils.isNotBlank(menuEntity.getName())){
                name = "["+menuEntity.getName()+"]"+menuEntity.getPath();
            }
            menuEntity.setName(name);
        }
        return JsonResponse.success(list, null);
    }
}
