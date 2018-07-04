package com.houjiahui.DyingWish.mapper;

import com.houjiahui.DyingWish.entity.User;
import com.houjiahui.core.persistence.BaseMapper;
import com.houjiahui.core.persistence.annotation.MyBatisMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface UserMapper extends BaseMapper<User> {
    public User selectUser(@Param(value="id")String id);
    public User getByLoginName(User entity);
    public List<User> findUserByPhone(User entity);
    public void updateUserInfo(User entity);
    public void updatePasswordById(User entity);
    public void updateLoginInfo(User entity);
    public List<User> testSql();
}
