package com.gnailcn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@EnableAuthorizationServer  //开启授权服务器的功能
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;    //注入密码加密器

    @Autowired
    private AuthenticationManager authenticationManager;  //注入认证管理器

    @Autowired
    private UserDetailsService userDetailsService;  //注入用户信息服务

//    @Autowired
//    private RedisConnectionFactory redisConnetionFactory;


    //配置客户端详情服务, 这个客户端是指哪些应用可以来申请令牌
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()      //意思是客户端信息存在内存中
                .withClient("coin-api")           //客户端id
                .secret(passwordEncoder.encode("coin-secret"))  //第三方客户端秘钥
                .scopes("all")  //客户端的权限范围
                .authorizedGrantTypes("password", "refresh_token")  //授权类型, 密码模式和刷新token
                .accessTokenValiditySeconds(7*24*3600)   //token有效期
                .refreshTokenValiditySeconds(30*24*3600)    //refresh_token有效期
                .and()
                .withClient("inside-app")
                .secret(passwordEncoder.encode("inside-secret"))
                .authorizedGrantTypes("client_credentials")
                .scopes("all")
                .accessTokenValiditySeconds(7*24*3600);

        super.configure(clients);
    }

    //配置验证管理器，UserDetailsService，加密方式
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(jwtTokenStore())  //使用jwt存储token
                .tokenEnhancer(jwtAccessTokenConverter()); //将登录实体转换为jwt token
        super.configure(endpoints);
    }

//    public TokenStore redisTokenStore(){
//        return new RedisTokenStore(redisConnetionFactory) ;
//    }

    private TokenStore jwtTokenStore() {
        JwtTokenStore jwtTokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        return jwtTokenStore;
    }

    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        ClassPathResource classPathResource = new ClassPathResource("coinexchange.jks");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, "coinexchange".toCharArray());
        tokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("coinexchange", "coinexchange".toCharArray()));
        return tokenConverter;
    }

}
