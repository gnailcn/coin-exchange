package com.gnailcn.controller;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gnailcn.domain.SysUserLog;
import com.gnailcn.model.R;
import com.gnailcn.service.SysUserLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "用户的操作记录查询")
@RequestMapping("/sysUserLog")
public class SysUserLogController {
    @Autowired
    private SysUserLogService sysUserLogService ;

    @GetMapping
    @ApiOperation(value = "分页查询用户的操作记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current" ,value = "当前页码"),
            @ApiImplicitParam(name = "size" ,value = "每页大小")
    })
    public R<Page<SysUserLog>> findByPage(@ApiIgnore Page<SysUserLog> page){
        page.addOrder(OrderItem.desc("created")) ;
        return R.ok(sysUserLogService.page(page)) ;
    }
}
