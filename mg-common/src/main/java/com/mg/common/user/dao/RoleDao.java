package com.mg.common.user.dao;

import com.mg.common.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by liukefu on 2016/1/9.
 */
public interface RoleDao extends JpaRepository<RoleEntity, String> {

  List<RoleEntity> findByName(String name);
}
