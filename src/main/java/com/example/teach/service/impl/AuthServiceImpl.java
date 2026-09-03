package com.example.teach.service.impl;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.User;
import com.example.teach.mapper.UserMapper;
import com.example.teach.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Override
    public ApiResult<Void> register(User user) {
        // 判断用户名是否已经存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return ApiResult.fail("用户名已被占用");
        }
        userMapper.insert(user);
        return ApiResult.ok();
    }

    @Override
    public ApiResult<String> login(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        // 用户不存在 或者密码不匹配
        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            return ApiResult.fail("用户名或者密码错误");
        }
        // 返回用户名给前端
        return ApiResult.ok(dbUser.getUsername());
    }
}
