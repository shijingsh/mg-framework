package com.mg.common.shiro.util;

import com.mg.common.entity.MenuEntity;
import com.mg.common.shiro.service.PermissionHelper;
import com.mg.common.shiro.vo.RequestUrlBean;
import com.mg.framework.log.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 拦截URL
 */
public class URLPermissionsFilter extends PermissionsAuthorizationFilter {
    private Logger Logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PermissionHelper permissionHelper;

    @Override
    public boolean isAccessAllowed(ServletRequest request,
                                   ServletResponse response, Object mappedValue) throws IOException {

        Session session = SecurityUtils.getSubject().getSession();
        session.getAttribute(Constants.CURRENT_USER);

        Map<String, MenuEntity> map = permissionHelper.getInterceptPath();
        RequestUrlBean bean = getRequestUrl(request);
        //绝对路径（包括queryStr）
        if (map.get(bean.getUrl()) != null) {
            return super.isAccessAllowed(request, response, buildPermissions(bean.getUrl()));
        }
        //url(不包含参数)
        if (!StringUtils.equals(bean.getUrl(),bean.getBaseUrl()) && map.get(bean.getBaseUrl()) != null) {
            return super.isAccessAllowed(request, response, buildPermissions(bean.getUrl()));
        }

        return true;
    }

    protected String[] buildPermissions(String url) {
        String[] perms = new String[1];
        perms[0] = url.toLowerCase();//path直接作为权限字符串
        return perms;
    }

    private RequestUrlBean getRequestUrl(ServletRequest request) {

        RequestUrlBean bean = new RequestUrlBean();
        HttpServletRequest req = (HttpServletRequest) request;
        String queryString = req.getQueryString();
        String url = req.getServletPath() + (StringUtils.isBlank(queryString) ? "" : "?" + queryString);

        bean.setContextPath(req.getContextPath());
        bean.setQueryStr(queryString);
        bean.setBaseUrl(req.getServletPath());
        bean.setUrl(url);

        return bean;
    }

}