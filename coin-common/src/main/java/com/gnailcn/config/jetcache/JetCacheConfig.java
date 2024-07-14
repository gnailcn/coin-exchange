package com.gnailcn.config.jetcache;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

@Configuration  // 用于定义配置类
@EnableCreateCacheAnnotation    // 启用@CreateCache注解
@EnableMethodCache(basePackages = "com.gnailcn.service.impl") // 启用@Cached注解
public class JetCacheConfig {

}

