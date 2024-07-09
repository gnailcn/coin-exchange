package com.gnailcn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {
    //获取当前用户信息
    //注意，下面的principal由spring security自动注入
    //当用户经过认证（用户名密码、token等）后，Spring Security
    // 将会创建一个 Principal 对象来表示当前用户的身份信息。
    @GetMapping("/user/info")
    public Principal userInfo(Principal principal) {
        return principal;
    }
}
