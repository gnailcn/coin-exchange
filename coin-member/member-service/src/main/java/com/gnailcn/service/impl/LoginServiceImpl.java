package com.gnailcn.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnailcn.feign.JwtToken;
import com.gnailcn.feign.OAuth2FeignClient;
import com.gnailcn.geetest.GeetestLib;
import com.gnailcn.geetest.GeetestLibResult;
import com.gnailcn.model.LoginForm;
import com.gnailcn.model.LoginUser;
import com.gnailcn.service.LoginService;
import com.gnailcn.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GeetestLib geetestLib;

    /**
     * 会员的登录
     * @param loginForm
     * @return
     */
    @Override
    public LoginUser login(LoginForm loginForm) {
        log.info("用户{}开始登录", loginForm.getUsername());
        checkFormData(loginForm);
        LoginUser loginUser = null;
        //登录就是使用用户名和密码换一个token而已-->远程调用-->authorization-server
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", loginForm.getUsername(), loginForm.getPassword(), "member_type", basicToken);
        if(tokenResponseEntity.getStatusCode() == HttpStatus.OK){
            JwtToken jwtToken = tokenResponseEntity.getBody();
            log.info("远程调用成功，结果为:", JSON.toJSONString(jwtToken, true));
            //返回给前端的token必须包含bearer前缀
            loginUser = new LoginUser(loginForm.getUsername(), jwtToken.getExpiresIn(),
                    jwtToken.getTokenType() + " " + jwtToken.getAccessToken(), jwtToken.getRefreshToken());
            //使用网关解决登出问题
            //注意存储的token是直接存的(没有bearer前缀的)
            strRedisTemplate.opsForValue().set(jwtToken.getAccessToken(), "", jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        }
        return loginUser;
    }

    /**
     * 检查登录表单数据
     * @param loginForm
     */
    private void checkFormData(LoginForm loginForm) {
        loginForm.check(geetestLib,redisTemplate);
    }

}
