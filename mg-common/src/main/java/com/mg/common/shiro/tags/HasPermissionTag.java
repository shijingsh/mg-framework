package com.mg.common.shiro.tags;

import com.mg.common.user.service.RoleService;
import com.mg.framework.log.ContextLookup;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.tags.PermissionTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HasPermissionTag extends PermissionTag {

	private Logger logger = LoggerFactory.getLogger(getClass());


	@Override
	protected boolean showTagBody(String p) {
        RoleService roleService = ContextLookup.getBean(RoleService.class);

		if(!StringUtils.contains(p, ":")) {
			return isPermitted(p);
		}

        String s[] = p.split(":");
        if(s.length != 3) {
            logger.warn("to access the resources error : {}", p);
            return false;
        }

		return true;//roleService.hasDisplayPermission(s[0], s[1], s[2]);
	}
}

