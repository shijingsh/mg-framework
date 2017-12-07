package com.mg.common.entity;

import com.mg.framework.entity.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统帐户规则表
 * Created by liukefu on 2015/12/7.
 */
@Entity
@Table(name="sys_user_rule")
public class UserRuleEntity  extends BaseEntity {

    private String loginName = "姓名";

    private String defaultPassword = "111111";

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDefaultPassword() {

        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }
}
