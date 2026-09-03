package com.example.teach.service;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.User;

public interface AuthService {

    ApiResult<Void> register(User user);

    ApiResult<String> login(User user);
}
