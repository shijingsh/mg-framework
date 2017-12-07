package com.mg.common.user.dao;

import com.mg.common.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 字段权限
 * Created by liukefu on 2016/3/15.
 */
public interface PermissionDao extends JpaRepository<PermissionEntity, String> {
}
