package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.entity.InstanceEntity;
import com.mg.common.user.service.UserService;
import com.mg.common.entity.UserEntity;
import com.mg.common.instance.service.InstanceService;
import com.mg.framework.log.Constants;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 用户登录/退出
 */
@Controller
@RequestMapping(value = "/",
        produces = "application/json; charset=UTF-8")
public class LoginController {
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private UserService userService;
    @Autowired
    private InstanceService instanceService;

    @ResponseBody
    @RequestMapping("/login")
    public String login() {

        String jsonString = WebUtil.getJsonBody(req);
        UserEntity userEntity = JSON.parseObject(jsonString, UserEntity.class);
        if(StringUtils.isBlank(userEntity.getLoginName()) || StringUtils.isBlank(userEntity.getPassword())){
            return JsonResponse.error(100000, "用户名,密码不能为空。");
        }

        Subject subject = SecurityUtils.getSubject();
        //判断是否启用多实例
        String userToken = getInstanceUserToken(userEntity);
        subject.getSession().setAttribute(Constants.TENANT_ID, null);
        //切换数据库到默认实例
        InstanceEntity instanceEntity = null;
        if (StringUtils.isNotBlank(userToken)) {
            instanceEntity = instanceService.findInstanceByToken(userToken);
        }
        if(instanceEntity!=null) {
            subject.getSession().setAttribute(Constants.TENANT_ID, instanceEntity.getId());
        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(userEntity.getLoginName(), userEntity.getPassword());
            subject.login(token);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResponse.error(100000, e.getMessage());
        }
        UserEntity user = userService.getUserById(UserHolder.getLoginUserId());
        user.setLastLoginDate(new Date());
        userService.updateUser(user);

        return JsonResponse.success(user, null);
    }

    /**
     * 获取公司实例
     * @param userEntity
     * @return
     */
    protected String getInstanceUserToken(UserEntity userEntity) {
       if(StringUtils.isNotBlank(userEntity.getUserToken())){
            return userEntity.getUserToken();
        }

        return null;
    }

    /**退出*/
    @ResponseBody
    @RequestMapping("/loginOut")
    public String loginOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return JsonResponse.success();
    }
}
