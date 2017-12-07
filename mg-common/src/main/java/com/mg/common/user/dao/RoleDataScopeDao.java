package com.mg.common.user.dao;

import com.mg.common.entity.RoleDataScopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 数据范围
 * Created by liukefu on 2016/3/16.
 */
public interface RoleDataScopeDao  extends JpaRepository<RoleDataScopeEntity, String> {
}
