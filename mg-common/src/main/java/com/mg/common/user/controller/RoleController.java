package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.entity.PermissionEntity;
import com.mg.common.entity.RoleDataScopeEntity;
import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.common.user.service.RoleService;
import com.mg.common.entity.RoleEntity;
import com.mg.common.entity.UserEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理
 * Created by liukefu.
 */
@Controller
@RequestMapping(value = "/role",
        produces = "application/json; charset=UTF-8")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private HttpServletRequest req;

    @RequestMapping("notAuthenticated")
    public String no() {
        return "/common/jsp/notAuthenticated.jsp";
    }

    /**
     * 根据ID，查询角色
     * @return
     */
    @ResponseBody
    @RequestMapping("/get")
    public String get(String id) {

        RoleEntity roleEntity = roleService.get(id);

        return JsonResponse.success(roleEntity, null);
    }

    /**
     * 保存角色
     * @return
     */
    @ResponseBody
    @RequestMapping("/post")
    public String post() {
        String jsonString = WebUtil.getJsonBody(req);

        RoleEntity roleEntity = JSON.parseObject(jsonString, RoleEntity.class);
        roleService.save(roleEntity);

        return JsonResponse.success(null, null);
    }

    /**
     * 添加角色的成员
     * @return
     */
    @ResponseBody
    @RequestMapping("/addUser")
    public String addUser(String id,String userId) {

        roleService.addUser(id, userId);

        return JsonResponse.success(null, null);
    }

    /**
     * 删除角色的成员
     * @return
     */
    @ResponseBody
    @RequestMapping("/removeUser")
    public String removeUser(String id,String userId) {

        roleService.removeUser(id, userId);

        return JsonResponse.success(null, null);
    }
    /**
     * 添加角色的成员
     * @return
     */
    @ResponseBody
    @RequestMapping("/addUserByEmp")
    public String addUserByEmp(String id,String empId) {

        roleService.addUserByEmp(id, empId);

        return JsonResponse.success(null, null);
    }
    /**
     * 分页查询角色
     * @return
     */
    @ResponseBody
    @RequestMapping("/list")
    public String getPageList() {
        String jsonString = WebUtil.getJsonBody(req);
        PageTableVO param = JSON.parseObject(jsonString, PageTableVO.class);

        PageTableVO vo = roleService.findPageList(param);

        return JsonResponse.success(vo, null);
    }

    /**
     * 角色下面的人员
     * @return
     */
    @ResponseBody
    @RequestMapping("/userList")
    public String usersInRole(String roleId) {

        RoleEntity roleEntity = roleService.get(roleId);
        List<UserEntity> roleList = roleService.findUsersById(roleId);
        roleEntity.setMembers(roleList);
        return JsonResponse.success(roleEntity, null);
    }

    /**
     * 角色下面的人员
     * @return
     */
    @ResponseBody
    @RequestMapping("/userListByName")
    public String usersInRoleName(String roleName) {
        String jsonString = WebUtil.getJsonBody(req);

        RoleEntity roleEntity = JSON.parseObject(jsonString, RoleEntity.class);
        RoleEntity role = roleService.getByName(roleEntity.getName());
        if(role!=null){
            List<UserEntity> roleList = roleService.findUsersById(role.getId());
            role.setMembers(roleList);
        }else {
            role.setMembers(new ArrayList<UserEntity>());
        }

        return JsonResponse.success(role, null);
    }

    /**
     * 删除角色的成员
     * @return
     */
    @ResponseBody
    @RequestMapping("/remove")
    public String remove(String id) {

        roleService.remove(id);

        return JsonResponse.success(null, null);
    }

    /**
     * 添加菜单授权
     * @return
     */
    @ResponseBody
    @RequestMapping("/addMenu")
    public String addMenu(MenuTypeEnum type) {
        String jsonString = WebUtil.getJsonBody(req);
        if(type==null){
            type = MenuTypeEnum.menu;
        }
        RoleEntity roleEntity = JSON.parseObject(jsonString, RoleEntity.class);
        roleService.addMenu(roleEntity, type);

        return JsonResponse.success(null, null);
    }

    /**
     * 添加字段授权
     * @return
     */
    @ResponseBody
    @RequestMapping("/addPermission")
    public String addPermission() {
        String jsonString = WebUtil.getJsonBody(req);

        RoleEntity roleEntity = JSON.parseObject(jsonString, RoleEntity.class);
        roleService.addPermission(roleEntity);

        return JsonResponse.success(null, null);
    }

    /**
     * 添加数据范围授权
     * @return
     */
    @ResponseBody
    @RequestMapping("/addDataScope")
    public String addDataScope(String roleId) {
        String jsonString = WebUtil.getJsonBody(req);

        RoleDataScopeEntity dataScopeEntity = JSON.parseObject(jsonString, RoleDataScopeEntity.class);
        roleService.addDataScope(dataScopeEntity,roleId);

        return JsonResponse.success(null, null);
    }

    /**
     * 所有的对象
     * @return
     */
    @ResponseBody
    @RequestMapping("/permissionObject")
    public String permissionObject(String roleId) {

        List<MObjectEntity> list = roleService.findPermissionObjects(roleId);

        return JsonResponse.success(list, null);
    }

    /**
     * 所有的对象
     * @return
     */
    @ResponseBody
    @RequestMapping("/dataScopeObject")
    public String dataScopeObject(String roleId) {

        List<MObjectEntity> list = roleService.findDataScopeObjects(roleId);

        return JsonResponse.success(list, null);
    }
    /**
     * 所有的字段权限
     * @return
     */
    @ResponseBody
    @RequestMapping("/fieldPermission")
    public String fieldPermission(String roleId,String objId) {

        //增加默认的权限（即无权限）
        List<PermissionEntity> list = roleService.findObjectPermission(roleId, objId);

        return JsonResponse.success(list, null);
    }

    /**
     * 所有的数据范围权限
     * @return
     */
    @ResponseBody
    @RequestMapping("/dataScopePermission")
    public String dataScopePermission(String roleId,String objId) {

        RoleDataScopeEntity dataScopeEntity = roleService.findDataScope(roleId, objId);

        return JsonResponse.success(dataScopeEntity, null);
    }
}
