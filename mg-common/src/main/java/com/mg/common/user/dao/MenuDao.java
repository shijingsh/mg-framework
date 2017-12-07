package com.mg.common.user.dao;

import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.common.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by liukefu on 2016/3/15.
 */
public interface MenuDao  extends JpaRepository<MenuEntity, String> {

   List<MenuEntity> findByPath(String path);

   List<MenuEntity> findByPathAndType(String path,MenuTypeEnum type);
}
