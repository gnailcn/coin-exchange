package com.gnailcn.controller;


import com.gnailcn.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "会员服务测试接口")
public class MemberTestController {

    @GetMapping("/test")
    @ApiOperation(value = "会员服务测试接口", authorizations = {@Authorization("Authorization")})
    public R<String> test() {
        return R.ok("会员服务测试成功");
    }
}
