package com.zhh.handsome.mapper;

import com.zhh.handsome.annotation.Delete;
import com.zhh.handsome.annotation.Insert;
import com.zhh.handsome.annotation.Mapper;
import com.zhh.handsome.annotation.Select;
import com.zhh.handsome.annotation.Update;
import com.zhh.handsome.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    // 查询单个用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
    
    // 查询所有用户
    @Select("SELECT * FROM user")
    List<User> selectAll();
    
    // 根据用户名查询用户
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
    
    // 插入用户
    @Insert("INSERT INTO user(username, password, email, phone, age) VALUES(#{username}, #{password}, #{email}, #{phone}, #{age})")
    int insert(User user);
    
    // 更新用户
    @Update("UPDATE user SET username = #{username}, password = #{password}, email = #{email}, phone = #{phone}, age = #{age} WHERE id = #{id}")
    int update(User user);
    
    // 删除用户
    @Delete("DELETE FROM user WHERE id = #{id}")
    int delete(Long id);
}