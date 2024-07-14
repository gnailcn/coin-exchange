package com.gnailcn.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//远程调用的返回结果
@Data
public class JwtToken {
    /**
     * access_token
     */
    // @JsonProperty注解是jackson的注解，用于将json的key与java对象的属性进行映射
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * token类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * refresh_token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 过期时间
     */
    @JsonProperty("expires_in")
    private Long expiresIn;


    /**
     * token的范围
     */
    private String scope;

    /**
     * 颁发的凭证
     */
    private String jti;
}
