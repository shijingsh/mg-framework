package com.mg.common.user.dao;

import com.mg.common.entity.UserRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by liukefu on 2015/12/7.
 */
public interface UserRuleDao  extends JpaRepository<UserRuleEntity, String> {

}