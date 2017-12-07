package com.mg.common.shiro.tags;

/**
 * Created by liukefu on 2016/1/18.
 */
import com.mg.common.shiro.service.RoleCacheService;
import com.mg.framework.log.ContextLookup;
import org.apache.shiro.web.tags.RoleTag;

public class LacksRoleTag extends RoleTag {
    public LacksRoleTag() {
    }

    protected boolean showTagBody(String roleName) {
        RoleCacheService roleCacheService = ContextLookup.getBean(RoleCacheService.class);
        Boolean hasRole = roleCacheService.hasRole(this.getSubject(), roleName);

        return !hasRole;
    }
}