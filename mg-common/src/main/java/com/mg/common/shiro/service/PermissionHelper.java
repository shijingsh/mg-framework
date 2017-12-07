package com.mg.common.shiro.service;

import com.mg.common.entity.RoleDataScopeEntity;
import com.mg.common.entity.RoleEntity;
import com.mg.common.entity.UserEntity;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.user.service.MenuService;
import com.mg.common.user.service.RoleService;
import com.mg.common.entity.MenuEntity;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 权限工具类
 * Created by liukefu on 2016/3/16.
 */
@Component
public class PermissionHelper {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MetaDataService metaDataService;
    /**
     * url资源缓存
     */
    private static Map<String,MenuEntity> map = new HashMap<>();

    @Transactional(readOnly = true)
    public Map<String, Set<String>> getRoleDataScopeMaps(RoleEntity role, UserEntity user) {
        Map<String, Set<String>> map = new HashMap<>();

        role = roleService.get(role.getId());
        List<RoleDataScopeEntity> dataScopeList = role.getDataScopeList();
        for (RoleDataScopeEntity dataScopeEntity:dataScopeList){

            MObjectEntity belongMObject = dataScopeEntity.getBelongMObject();
            MExpressGroupEntity dataScope = dataScopeEntity.getDataScope();
            dataScope.setPageSize(-1);
            List<String> ids = metaDataService.queryIds(belongMObject, dataScope);
            Set<String> set = new HashSet<>();
            set.addAll(ids);
            map.put(belongMObject.getId(),set);
        }

        return map;
    }

    public Map<String,MenuEntity> getInterceptPath(){

        if(map.isEmpty()){
            List<MenuEntity> list = menuService.findALL();
            for(MenuEntity menuEntity:list){
                map.put(menuEntity.getPath(),menuEntity);
            }
        }

        return map;
    }
}
