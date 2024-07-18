package com.gnailcn.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authorization-server")
public interface OAuth2FeignClient {
    @PostMapping("/oauth/token")
    ResponseEntity<JwtToken> getToken(
            @RequestParam("grant_type") String grantType,   //授权类型
            @RequestParam("username")    String username,   //用户名
            @RequestParam("password")    String password,   //用户密码
            @RequestParam("login_type")    String loginType,  //登录类型
            @RequestHeader("Authorization") String basicToken   //由第三方客户端Basic auth加密而成: Basic dXNlcjpwYXNzd29yZA==
    );
}
