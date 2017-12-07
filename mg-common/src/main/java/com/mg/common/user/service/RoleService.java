package com.mg.common.user.service;

import com.mg.common.entity.PermissionEntity;
import com.mg.common.entity.RoleDataScopeEntity;
import com.mg.common.entity.RoleEntity;
import com.mg.common.entity.UserEntity;
import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;

import java.util.List;

/**
 * Created by liukefu on 2016/1/9.
 */
public interface RoleService {

    /**
     * 获取一个角色
     * @param id
     * @return
     */
    public RoleEntity get(String id);

    /**
     * 根据名称查询角色
     * @param name
     * @return
     */
    public RoleEntity getByName(String name);
    /**
     * 保存
     * @param roleEntity
     * @return
     */
    public RoleEntity save(RoleEntity roleEntity);

    /**
     * 删除
     * @param id
     */
    public void remove(String id);
    /**
     * 根据id 查询角色下面的人员
     * @param id
     */
    public List<UserEntity> findUsersById(String id);

    /**
     * 查询列表
     * @param pageTableVO
     * @return
     */
    public PageTableVO findPageList(PageTableVO pageTableVO);

    /**
     * 角色下面添加人员
     * @param id
     * @param userId
     */
    void addUser(String id, String userId);

    /**
     * 角色下面删除人员
     * @param id
     * @param userId
     */
    public void removeUser(String id, String userId);
    /**
     * 角色下面添加人员
     * @param id
     * @param empId
     */
    public void addUserByEmp(String id, String empId);

    /**
     * 根据用户查询用户的所有角色
     * @param userEntity
     * @return
     */
    public List<RoleEntity> findList(UserEntity userEntity);

    /**
     * 角色添加菜单权限
     * @param roleEntity
     * @param type
     */
    void addMenu(RoleEntity roleEntity,MenuTypeEnum type);

    /**
     * 角色添加字段权限
     * @param roleEntity
     */
    void addPermission(RoleEntity roleEntity);

    /**
     * 角色添加数据范围
     * @param dataScopeEntity
     * @param roleId
     */
    void addDataScope(RoleDataScopeEntity dataScopeEntity , String roleId);

    /**
     * 所有字段权限
     * @param roleId
     * @return
     */
    List<PermissionEntity> findPermissionList(String roleId);

    /**
     * 所有字段权限
     * @param roleId
     * @param objId
     * @return
     */
    List<PermissionEntity> findALLPermission(String roleId, String objId);

    /**
     * 所有数据数据范围权限
     * @param roleId
     * @param objId
     * @return
     */
    RoleDataScopeEntity findDataScope(String roleId, String objId);

    /**
     * 角色中，已经赋权的元数据对象
     * @param roleId
     * @return
     */
    List<MObjectEntity> findPermissionObjects(String roleId);
    /**
     * 角色中，已经赋数据范围权限的元数据对象
     * @param roleId
     * @return
     */
    List<MObjectEntity> findDataScopeObjects(String roleId);

    /**
     * 初始化角色对象字段权限
     * @param roleId
     * @param objId
     * @return
     */
    List<PermissionEntity> findObjectPermission(String roleId, String objId);

    List<String> getUrlPermission(List<String> roleIdList);
}
