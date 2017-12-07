package com.mg.common.user.dao;

import com.mg.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<UserEntity, String> {
    UserEntity findByName(String name);
}
