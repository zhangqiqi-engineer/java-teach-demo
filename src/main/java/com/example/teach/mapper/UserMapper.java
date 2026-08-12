package com.example.teach.mapper;

import com.example.teach.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);
    int insert(User user);
}