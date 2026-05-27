package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.LoginRequest;
import com.lifestyle.platform.dto.RegisterRequest;
import com.lifestyle.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
