package com.gnailcn.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gnailcn.domain.UserWallet;
import com.gnailcn.model.R;
import com.gnailcn.service.UserWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@Api(tags = "用户的提币地址")
@RequestMapping("/userWallets")
public class UserWalletController {
    @Autowired
    private UserWalletService userWalletService;

    @GetMapping
    @ApiOperation(value = "分页查询用户的提币地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户的id"),
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数")
    })
    @PreAuthorize("hasAuthority('user_wallet_query')")
    public R<Page<UserWallet>> findByPage(@ApiIgnore Page<UserWallet> page, Long userId) {
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<UserWallet> userWalletPage = userWalletService.findByPage(page, userId);
        return R.ok(userWalletPage);
    }

    @GetMapping("/getCoinAddress/{coinId}")
    @ApiOperation(value = "查询用户某种币的提币地址")
    @ApiImplicitParam(name = "coinId", value = "币种的id")
    public R<List<UserWallet>> getCoinAddress(@PathVariable("coinId") Long coinId) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<UserWallet> userWalletList = userWalletService.findUserWallets(userId, coinId);
        return R.ok(userWalletList);
    }

    @PostMapping
    @ApiOperation(value = "新增用户的提币地址")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "userWallet", value = "user wallet json 数据")
    )
    public R save(@RequestBody @Validated UserWallet userWallet) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        userWallet.setUserId(userId);
        boolean save = userWalletService.save(userWallet);
        if(save) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @PostMapping("/deleteAddress")
    @ApiOperation(value = "删除某个用户的提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId" ,value = "提现地址的ID" ) ,
            @ApiImplicitParam(name = "payPassword" ,value = "交易密码")
    })
    public R delete(@RequestParam(required = true) Long addressId ,@RequestParam(required = true) String payPassword){
        boolean isOk =  userWalletService.deleteUserWallet(addressId,payPassword) ;
        if(isOk){
            return R.ok("删除成功") ;
        }
        return R.fail("删除失败") ;
    }
}
