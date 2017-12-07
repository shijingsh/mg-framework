package com.mg.common.shiro.service;

import com.mg.common.entity.RoleEntity;
import com.mg.common.user.dao.RoleDao;
import com.mg.framework.cache.SimpleCacheProvider;
import com.mg.framework.utils.StatusEnum;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 判断是否有指定角色
 * Created by liukefu on 2016/1/10.
 */
@Service
public class RoleCacheService {
    @Autowired
    private RoleDao roleDao;
    public static SimpleCacheProvider cacheProvider = SimpleCacheProvider.getInstance();

    public void init(){
        if(cacheProvider==null) {
            cacheProvider = SimpleCacheProvider.getInstance();
        }
        List<RoleEntity> list = roleDao.findAll();
        for(RoleEntity roleEntity:list){
            cacheProvider.put(roleEntity.getName(),roleEntity.getId());
        }
    }

    public boolean hasRole(Subject subject,String roleName){
        if(cacheProvider==null){
            init();
        }
        if(roleName!=null){
            roleName = roleName.trim();
        }

        if(cacheProvider.get(roleName)==null){
            //不存在角色，则创建一个角色
            if(StringUtils.isNotBlank(roleName)){
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setName(roleName);
                roleEntity.setStatus(StatusEnum.STATUS_VALID);
                roleDao.saveAndFlush(roleEntity);

                cacheProvider.put(roleEntity.getName(),roleEntity.getId());
            }
        }
        //如果有admin 权限 拥有所有权限
        if(UserHolder.getLoginUser().isAdmin()){
            return true;
        }
        return subject != null && subject.hasRole(roleName);
    }

    public boolean hasAnyRole(Subject subject,String roleNames){

        String[] arr = roleNames.split(",");
        boolean hasAnyRole = false;
        for(String roleName:arr){
            hasAnyRole = hasRole(subject,roleName);
            if(hasAnyRole){
                return hasAnyRole;
            }
        }

        return hasAnyRole;
    }
}
