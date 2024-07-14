package com.gnailcn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.gnailcn.domain.SysMenu;
import com.gnailcn.feign.JwtToken;
import com.gnailcn.feign.OAuth2FeignClient;
import com.gnailcn.model.LoginResult;
import com.gnailcn.service.SysLoginService;
import com.gnailcn.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}开始登录", username);

        // 1 获取token 需要远程调用authorization-server 该服务
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", username, password, "admin_type", basicToken);
        if(tokenResponseEntity.getStatusCode() != HttpStatus.OK){
            throw new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken jwtToken = tokenResponseEntity.getBody();
        log.info("远程调用授权服务器成功， 获取到token:{}", JSON.toJSONString(jwtToken, true));
        String token = jwtToken.getAccessToken();

        // 2 查询我们的菜单数据
        Jwt jwt = JwtHelper.decode(token);
        String jwtJsonStr = jwt.getClaims();
        JSONObject jsonObject = JSON.parseObject(jwtJsonStr);
        Long userId = Long.valueOf(jsonObject.getString("user_name"));  //jwt中已经包含了用户id（叫user_name）
        List<SysMenu> menus = sysMenuService.getMenusByUserId(userId);

        // 3 权限数据怎么查询, 不需要查询，因为jwt里面已经包含了
        JSONArray authoritiesJsonArray = jsonObject.getJSONArray("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesJsonArray.stream()
                .map(authorityJson->new SimpleGrantedAuthority(authorityJson.toString()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        //1 将token存储在redis里面，配置我们的网关做jwt验证的操作
        redisTemplate.opsForValue().set(token, "", jwtToken.getExpiresIn(), TimeUnit.SECONDS);

        //2.给前端返回的Token数据需要加上bearer
        return new LoginResult(jwtToken.getTokenType() + " " + token, menus, authorities);
    }
}
