package com.mg.framework.utils;

import com.mg.common.entity.UserEntity;
import com.mg.framework.log.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  提供会话中的一些主要常量，如登录者的用户ID等.
 *
 */
public class UserHolder {
    private static Logger logger = LoggerFactory.getLogger(UserHolder.class);

    /**
     * 获得当前登录者User
     */
    public static UserEntity getLoginUser() {
        Session session = SecurityUtils.getSubject().getSession();
        UserEntity user = (UserEntity) session.getAttribute(Constants.CURRENT_USER);

        return user;
    }

    /**
     * 获得当前登录者User
     */
    public static String getLoginUserEmployeeId() {
        Session session = SecurityUtils.getSubject().getSession();
        UserEntity user = (UserEntity) session.getAttribute(Constants.CURRENT_USER);

        String employeeId = user.getEmployeeId();
        if(StringUtils.isBlank(employeeId)){
            return "userId"+user.getId();
        }

        return employeeId;
    }
    /**
     * 获得当前登录者的User ID
     */
    public static String getLoginUserId() {
        Session session = SecurityUtils.getSubject().getSession();
        UserEntity user = (UserEntity) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    /**
     * 获得当前登录者的User Name
     */
    public static String getLoginUserName() {
        Session session = SecurityUtils.getSubject().getSession();
        UserEntity user = (UserEntity) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return null;
        }
        return user.getName();
    }


    /**
     * 获得当前登录者的company Name
     */
    public static String getLoginCompanyName() {
        Session session = SecurityUtils.getSubject().getSession();
        return session.getAttribute(Constants.TENANT_COMPANY) + "";
    }


//    public static String getUserToken() {
//        Session session = SecurityUtils.getSubject().getSession();
//        UserEntity user = (UserEntity) session.getAttribute(Constants.CURRENT_USER);
//        if (user == null) {
//            return null;
//        }
//        return user.getUserToken();
//    }



    /**
     * 获得当前登录者的User instanceId
     */
    public static String getLoginUserTenantId() {
        Session session = SecurityUtils.getSubject().getSession();
        return session.getAttribute(Constants.TENANT_ID) + "";
    }

    /**
     * 获得当前登录者的User instanceId tokey
     */
//    public static String getLoginUserToken() {
//        Session session = SecurityUtils.getSubject().getSession();
//        return session.getAttribute(Constants.TENANT_TOKEN) + "";
//    }
}
