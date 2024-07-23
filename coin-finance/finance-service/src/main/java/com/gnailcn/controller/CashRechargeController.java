package com.gnailcn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gnailcn.domain.CashRecharge;
import com.gnailcn.domain.CashRechargeAuditRecord;
import com.gnailcn.model.CashParam;
import com.gnailcn.model.R;
import com.gnailcn.service.CashRechargeService;
import com.gnailcn.vo.CashTradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/cashRecharges")
@Api(value = "GCN控制器")
public class CashRechargeController {
    @Autowired
    private CashRechargeService cashRechargeService ;

    /**
     * 分页条件查询用户充值记录
     * @param page
     * @param coinId
     * @param userId
     * @param userName
     * @param mobile
     * @param status
     * @param numMin
     * @param numMax
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "coinId", value = "币种ID"),
            @ApiImplicitParam(name = "userId", value = "用户的ID"),
            @ApiImplicitParam(name = "userName", value = "用户的名称"),
            @ApiImplicitParam(name = "mobile", value = "用户的手机号"),
            @ApiImplicitParam(name = "status", value = "充值的状态"),
            @ApiImplicitParam(name = "numMin", value = "充值金额的最小值"),
            @ApiImplicitParam(name = "numMax", value = "充值金额的最小值"),
            @ApiImplicitParam(name = "startTime", value = "充值开始时间"),
            @ApiImplicitParam(name = "endTime", value = "充值结束时间"),
    })
    public R<Page<CashRecharge>> findByPage(
            @ApiIgnore Page<CashRecharge> page, Long coinId,
            Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax,
            String startTime, String endTime
    ) {
        Page<CashRecharge> pageData =  cashRechargeService.findByPage(page,coinId,userId,userName,
                mobile,status,numMin,numMax,startTime,endTime) ;
        return R.ok(pageData) ;
    }

    @PostMapping("/cashRechargeUpdateStatus")
    @ApiOperation(value = "现金充值审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cashRechargeAuditRecord", value = "")
    })
    public R cashRechargeUpdateStatus(@RequestBody CashRechargeAuditRecord cashRechargeAuditRecord){
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean isOk = cashRechargeService.cashRechargeAudit(Long.valueOf(userIdStr), cashRechargeAuditRecord);
        if(isOk){
            return R.ok();
        }
        return R.fail("现金充值审核失败");
    }

    @GetMapping("/user/records")
    @ApiOperation(value = "查询当前用户的充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页") ,
            @ApiImplicitParam(name = "size",value = "每页显示的大小") ,
            @ApiImplicitParam(name = "status",value = "充值的状态") ,
    })
    public R<Page<CashRecharge>> findUserCashRecharge(@ApiIgnore Page<CashRecharge> page ,Byte status){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) ;
        Page<CashRecharge> cashRechargePage = cashRechargeService.findUserCashRecharge(page ,userId,status) ;
        return R.ok(cashRechargePage) ;
    }

    @PostMapping("/buy")
    @ApiOperation(value = "GCN的充值操作")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "cashParam",value = "现金交易的参数")
    })
    public R<CashTradeVo> buy(@RequestBody @Validated CashParam cashParam){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) ;
        CashTradeVo cashTradeVo = cashRechargeService.buy(userId,cashParam) ;
        return R.ok(cashTradeVo) ;
    }
}
