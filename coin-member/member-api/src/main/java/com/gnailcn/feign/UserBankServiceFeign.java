package com.gnailcn.feign;

import com.gnailcn.config.feign.OAuth2FeignConfig;
import com.gnailcn.dto.UserBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 若FeignClient里面的name相同时，spring创建对象会报错，认为他们是同一个对象, 所以需要加上contextId来区分
 */
@FeignClient(name = "member-service", contextId = "userBankServiceFeign", configuration = OAuth2FeignConfig.class, path = "/userBanks")
public interface UserBankServiceFeign {

    @GetMapping("/{userId}/info")
    UserBankDto getUserBankInfo(@PathVariable Long userId);
}
