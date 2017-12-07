package com.mg.common.shiro.tags;

/**
 * Created by liukefu on 2016/1/10.
 */
import com.mg.common.shiro.service.RoleCacheService;
import com.mg.framework.log.ContextLookup;
import org.apache.shiro.web.tags.RoleTag;

public class HasRoleTag extends RoleTag {
    public HasRoleTag() {
    }

    protected boolean showTagBody(String roleName) {
        RoleCacheService roleCacheService = ContextLookup.getBean(RoleCacheService.class);

        return roleCacheService.hasRole(this.getSubject(),roleName);
    }
}