package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.entity.UserRuleEntity;
import com.mg.common.user.service.UserRuleService;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录帐号规则接口
 * Created by liukefu on 2015/12/8.
 */
@Controller
@RequestMapping(value = "/userRule",
        produces = "application/json; charset=UTF-8")
public class UserRuleController {

    @Autowired
    private UserRuleService userRuleService;
    @Autowired
    private HttpServletRequest req;
    /**
     * 初始化用户登录帐号的密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/get")
    public String get() {

        UserRuleEntity userRuleEntity = userRuleService.get();

        return JsonResponse.success(userRuleEntity, null);
    }

    @ResponseBody
    @RequestMapping("/post")
    public String post() {
        String jsonString = WebUtil.getJsonBody(req);

        UserRuleEntity userRuleEntity = JSON.parseObject(jsonString, UserRuleEntity.class);
        userRuleService.saveRule(userRuleEntity);

        return JsonResponse.success(null, null);
    }
}
