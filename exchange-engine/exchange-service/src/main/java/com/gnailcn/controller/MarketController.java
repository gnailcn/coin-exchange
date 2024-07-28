package com.gnailcn.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gnailcn.domain.Market;
import com.gnailcn.domain.TurnoverOrder;
import com.gnailcn.dto.MarketDto;
import com.gnailcn.feign.MarketServiceFeign;
import com.gnailcn.model.R;
import com.gnailcn.service.MarketService;
import com.gnailcn.service.TurnoverOrderService;
import com.gnailcn.vo.DepthItemVo;
import com.gnailcn.vo.DepthsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/markets")
@Api(tags = "交易市场的控制器")
public class MarketController implements MarketServiceFeign {

    @Autowired
    private MarketService marketService;

    @Autowired
    private TurnoverOrderService turnoverOrderService;

    @GetMapping
    @ApiOperation(value = "交易市场的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示的条数"),
            @ApiImplicitParam(name = "tradeAreaId", value = "交易区域的ID"),
            @ApiImplicitParam(name = "status", value = "交易市场的状态"),
    })
    @PreAuthorize("hasAuthority('trade_market_query')")
    public R<Page<Market>> findByPage(@ApiIgnore Page<Market> page, Long tradeAreaId, Byte status) {
        Page<Market> pageData = marketService.findByPage(page, tradeAreaId, status);
        return R.ok(pageData);
    }

    @PostMapping("/setStatus")
    @ApiOperation(value = "启用/禁用交易市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "market的json数据")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R setStatus(@RequestBody Market market) {
        boolean updateById = marketService.updateById(market);
        if (updateById) {
            return R.ok();
        }
        return R.fail("状态设置失败");
    }

    @PostMapping
    @ApiOperation(value = "新增一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_create')")
    public R save(@RequestBody Market market) {
        boolean save = marketService.save(market);
        if (save) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market", value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R update(@RequestBody Market market) {
        boolean updateById = marketService.updateById(market);
        if (updateById) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @GetMapping("/all")
    @ApiOperation(value = "查询所有的交易市场")
    public R<List<Market>> listMarks() {
        return R.ok(marketService.list());
    }

    /**
     * 使用报价货币 以及 出售的货币的ID
     *
     * @param buyCoinId
     * @param sellCoinId
     * @return
     */
    @Override
    public MarketDto findByCoinId(Long buyCoinId, Long sellCoinId) {
        MarketDto marketDto = marketService.findByCoinId(buyCoinId, sellCoinId);
        return marketDto;
    }

    @ApiOperation(value = "通过的交易对以及深度查询当前的市场的深度数据")
    @GetMapping("/depth/{symbol}/{dept}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对"),
            @ApiImplicitParam(name = "dept", value = "深度类型"),
    })
    public R<DepthsVo> findDeptVosSymbol(@PathVariable("symbol") String symbol, String dept) {
        // 交易市场
        Market market = marketService.getMarkerBySymbol(symbol);

        DepthsVo depthsVo = new DepthsVo();
        depthsVo.setCnyPrice(market.getOpenPrice()); // CNY的价格
        depthsVo.setPrice(market.getOpenPrice()); // GCN的价格

//        Map<String, List<DepthItemVo>> depthMap = orderBooksFeignClient.querySymbolDepth(symbol);
//        if (!CollectionUtils.isEmpty(depthMap)) {
//            depthsVo.setAsks(depthMap.get("asks"));
//            depthsVo.setBids(depthMap.get("bids"));
//        }

        //先用假数据
        depthsVo.setAsks(Arrays.asList(
                new DepthItemVo(BigDecimal.valueOf(7.00000), BigDecimal.valueOf(100)),
                new DepthItemVo(BigDecimal.valueOf(6.00000), BigDecimal.valueOf(200)),
                new DepthItemVo(BigDecimal.valueOf(5.00000), BigDecimal.valueOf(300)),
                new DepthItemVo(BigDecimal.valueOf(4.00000), BigDecimal.valueOf(400)),
                new DepthItemVo(BigDecimal.valueOf(3.00000), BigDecimal.valueOf(500)),
                new DepthItemVo(BigDecimal.valueOf(2.00000), BigDecimal.valueOf(600)),
                new DepthItemVo(BigDecimal.valueOf(1.00000), BigDecimal.valueOf(700))
        ));
        depthsVo.setBids(Arrays.asList(
                new DepthItemVo(BigDecimal.valueOf(1.00000), BigDecimal.valueOf(100)),
                new DepthItemVo(BigDecimal.valueOf(2.00000), BigDecimal.valueOf(200)),
                new DepthItemVo(BigDecimal.valueOf(3.00000), BigDecimal.valueOf(300)),
                new DepthItemVo(BigDecimal.valueOf(4.00000), BigDecimal.valueOf(400)),
                new DepthItemVo(BigDecimal.valueOf(5.00000), BigDecimal.valueOf(500)),
                new DepthItemVo(BigDecimal.valueOf(6.00000), BigDecimal.valueOf(600)),
                new DepthItemVo(BigDecimal.valueOf(7.00000), BigDecimal.valueOf(700))
        ));
        return R.ok(depthsVo);
    }

    @ApiOperation(value = "查询成交记录")
    @GetMapping("/trades/{symbol}")
    public R<List<TurnoverOrder>> findSymbolTurnoverOrder(@PathVariable("symbol") String symbol) {
        List<TurnoverOrder> turnoverOrders = turnoverOrderService.findBySymbol(symbol);
        return R.ok(turnoverOrders);
    }
}
