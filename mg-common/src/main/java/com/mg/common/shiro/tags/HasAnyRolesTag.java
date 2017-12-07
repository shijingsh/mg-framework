package com.mg.common.shiro.tags;

/**
 * Created by liukefu on 2016/1/10.
 */
import com.mg.common.shiro.service.RoleCacheService;
import com.mg.framework.log.ContextLookup;
import org.apache.shiro.web.tags.RoleTag;

public class HasAnyRolesTag extends RoleTag {

    public HasAnyRolesTag() {
    }

    protected boolean showTagBody(String roleNames) {
        RoleCacheService roleCacheService = ContextLookup.getBean(RoleCacheService.class);
        return roleCacheService.hasAnyRole(this.getSubject(),roleNames);
    }
}
