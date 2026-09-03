package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.User;
import com.example.teach.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody User user) {
        return authService.register(user);
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody User user) {
        return authService.login(user);
    }
}
