package com.mg.common.shiro.util;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

/**
 * @author Tan Liang (Bred Tan)
 * @since 2014/10/5
 */
public class WildcardPermissionExResolver implements PermissionResolver {
	
	public WildcardPermissionExResolver() {
		super();
	}

	@Override
	public Permission resolvePermission(String permissionString) {
		return new StringPermission(permissionString);
	}
}
