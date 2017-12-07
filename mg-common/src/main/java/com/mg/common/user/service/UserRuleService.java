package com.mg.common.user.service;

import com.mg.common.entity.UserEntity;
import com.mg.common.entity.UserRuleEntity;

/**
 * Created by liukefu on 2015/12/7.
 */
public interface UserRuleService {

    /**
     * 获取系统帐户规则
     *
     * @return
     */
    public UserRuleEntity get();
    /**
     * 保存
     * @param userRuleEntity
     * @return
     */
    public UserRuleEntity saveRule(UserRuleEntity userRuleEntity);

    /**
     * 初始化登录用户信息
     * @param userEntity
     * @param initPass 是否重置密码
     * @return
     */
    public UserEntity initUser(UserEntity userEntity, boolean initPass);
}
