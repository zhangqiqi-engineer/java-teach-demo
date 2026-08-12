package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.User;
import com.example.teach.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserMapper userMapper;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody User user) {
        // 判断用户名是否已经存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return ApiResult.fail("用户名已被占用");
        }
        userMapper.insert(user);
        return ApiResult.ok();
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        // 用户不存在 或者密码不匹配
        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            return ApiResult.fail("用户名或者密码错误");
        }
        // 返回用户名给前端
        return ApiResult.ok(dbUser.getUsername());
    }
}