package com.mg.common.shiro.service;

import com.mg.common.entity.MenuEntity;
import com.mg.common.entity.PermissionEntity;
import com.mg.common.entity.RoleEntity;
import com.mg.common.entity.UserEntity;
import com.mg.common.metadata.service.MetaDataManageService;
import com.mg.common.shiro.util.AdminPermission;
import com.mg.common.shiro.util.AuthorizationHelper;
import com.mg.common.shiro.util.WildcardPermissionEx;
import com.mg.common.shiro.util.WildcardPermissionExResolver;
import com.mg.common.user.service.MenuService;
import com.mg.common.user.service.RoleService;
import com.mg.common.user.service.UserService;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.log.Constants;
import com.mg.framework.utils.StatusEnum;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionHelper permissionHelper;
    @Autowired
    private MenuService menuService;
    @Autowired
    private MetaDataManageService metaDataManageService;
    public UserRealm() {
        super();
        setName("userRealm");

        setAuthenticationCacheName(AuthorizationHelper.SHIRO_CACHE_NAME);

        //处理权限比较方法, 自定义比较方法
        setPermissionResolver(new WildcardPermissionExResolver());
    }

    /**
     * 授权
     * @param principals
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        String[] names = StringUtils.split(username, ":");

        String loginUserName = names[0];
        String companyInstanceName = null;
        if(names.length>1){
            companyInstanceName = names[1];
        }

        return getSimpleAuthorizationInfo(loginUserName, companyInstanceName);
    }

    /**
     * 身份认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();  //得到用户名
        String password = new String((char[])token.getCredentials()); //得到密码
        UserEntity user = userService.getUser(username,password);

        if (user == null) {
            throw new UnknownAccountException("用户名或是密码错误");
        }
        if(user.getStatus() == StatusEnum.STATUS_INVALID) {
            throw new LockedAccountException("账号已失效，请联系管理员。");
        }
        Session session = SecurityUtils.getSubject().getSession();
        //设置用户角色
        List<RoleEntity> roleEntities = roleService.findList(user);

        user.setRoles(roleEntities);
        session.setAttribute(Constants.CURRENT_USER, user);

        List<MenuEntity> urlList = menuService.findMyUrls();
        Map<String,Object> urlAllMap = menuService.findAllUrls();
        List<MObjectEntity> objectList = metaDataManageService.findAllObject();
        session.setAttribute(Constants.CURRENT_USER_URLS, urlList);
        session.setAttribute(Constants.CURRENT_USER_URLS_ALL, urlAllMap);
        session.setAttribute(Constants.CURRENT_USER_OBJECT, objectList);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username, user.getPassword().toCharArray(), getName());
        //清空集合和清空授权, 防止用户非法退出登录,
        //而保存本地的cache尚未清空, 导致下次同用户无法登录查看权限
        clearCachedAuthorizationInfo(authenticationInfo.getPrincipals());

        //清除掉之前的权限信息以便重新加载
        this.clearCachedAuthorizationInfo(new SimplePrincipalCollection(username, "userRealm"));

        return authenticationInfo;
    }

    private SimpleAuthorizationInfo getSimpleAuthorizationInfo(String loginUserName, String companyInstanceName) {
        UserEntity user = UserHolder.getLoginUser();
        if (user == null) {
            return null;
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        if(user.isAdmin()){
            AdminPermission adminPermission = new AdminPermission();
            info.addObjectPermission(adminPermission);
            return info;
        }

        List<RoleEntity> roles = user.getRoles();
        if (roles == null || roles.size() == 0) {
            return info;
        }

        WildcardPermissionEx permission = new WildcardPermissionEx();
        List<String> roleIdList=new ArrayList<>();
        for (RoleEntity role : roles) {
            if (role.getStatus() == StatusEnum.STATUS_VALID) {
                info.addRole(role.getName());

                Map<String, Set<String>> dataScopeMap = permissionHelper.getRoleDataScopeMaps(role, user);
                List<PermissionEntity> permissionEntityList = roleService.findPermissionList(role.getId());
                for (PermissionEntity permissionEntity : permissionEntityList) {
                    //获取ID集合
                    Set<String> ids = dataScopeMap.get(permissionEntity.getBelongMObject().getId());

                    permission.addPropertyPermission(permissionEntity, ids);
                }
                roleIdList.add(role.getId());
            }
        }

        //url权限
        List<String> urlPermissions = roleService.getUrlPermission(roleIdList);
        permission.addFunctionPermissions(urlPermissions);

        info.addObjectPermission(permission);

        return info;
    }
}
