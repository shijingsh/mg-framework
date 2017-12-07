package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.components.SmsService;
import com.mg.common.entity.UserEntity;
import com.mg.common.user.service.UserService;
import com.mg.framework.utils.JsonResponse;
import com.mg.framework.utils.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户注册
 * Created by kf on 2016/11/11.
 */
@Controller
@RequestMapping(value = "/",
        produces = "application/json; charset=UTF-8")
public class RegisterController {
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;
    @ResponseBody
    @RequestMapping("/register")
    public String register() {
        String jsonString = WebUtil.getJsonBody(req);
        UserEntity userEntity = JSON.parseObject(jsonString, UserEntity.class);
        if (StringUtils.isBlank(userEntity.getLoginName()) || StringUtils.isBlank(userEntity.getPassword())) {
            return JsonResponse.error(100000, "用户名,密码不能为空。");
        }

        UserEntity user = userService.getUser(userEntity.getLoginName());
        if (user != null) {
            return JsonResponse.error(100000, "用户已注册");
        }
        if(StringUtils.isBlank(userEntity.getMobile())){
            userEntity.setMobile(userEntity.getLoginName());
        }
        String code = req.getParameter("code").trim();
        if(smsService.validateCode(userEntity.getMobile(),code)){
            userService.updateUser(userEntity);
        }else{
            return JsonResponse.error(100000, "验证码输入错误");
        }

        return JsonResponse.success(userEntity);
    }
}
