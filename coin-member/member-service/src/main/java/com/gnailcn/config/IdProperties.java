package com.gnailcn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identify")
@Data
public class IdProperties {

    //身份认证的url地,
    private String url; //https://idcert.market.alicloudapi.com/idcard?idCard=%s&name=%s

    //购买的appKey
    private String appKey ;

    /***
     * 你购买的appSecret
     */
    private String appSecret ;

    /***
     * 你购买的appCode
     */
    private String appCode ;
}
