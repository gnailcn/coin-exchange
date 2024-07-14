package com.gnailcn.config.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;

@Configuration
@EnableResourceServer   //资源服务器
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启方法级别的权限控制
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .sessionManagement().disable()
                .authorizeRequests()
                .antMatchers(
                        "/users/setPassword" ,
                        "/users/register",
                        "/sms/sendTo",
                        "/gt/register" ,
                        "/login" ,
                        "/v2/api-docs",
                        "/swagger-resources/configuration/ui",//用来获取支持的动作
                        "/swagger-resources",//用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()
                .antMatchers("/**").authenticated()
                .and().headers().cacheControl();
    }

    /**
     * 设置公钥
     * @param resources
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(jwtTokenStore());
    }

    private TokenStore jwtTokenStore() {
        JwtTokenStore jwtTokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        return jwtTokenStore;
    }

    @Bean   //放在ioc容器中
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        //资源服务器仅验证token（使用公钥）,不需要产生token（使用私钥）
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        String publicKey = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("coinexchange.txt");
            byte[] bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            publicKey = new String(bytes);
        } catch(Exception e) {
            e.printStackTrace();
        }
        tokenConverter.setVerifierKey(publicKey);
        return tokenConverter;
    }
}
