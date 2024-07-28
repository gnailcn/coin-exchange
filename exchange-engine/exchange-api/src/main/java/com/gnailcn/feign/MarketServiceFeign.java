package com.gnailcn.feign;

import com.gnailcn.config.feign.OAuth2FeignConfig;
import com.gnailcn.dto.MarketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service", contextId = "marketServiceFeign", configuration = OAuth2FeignConfig.class, path = "/markets")
public interface MarketServiceFeign {
    /**
     * 使用报价货币 以及 出售的货币的ID
     *
     * @param buyCoinId
     * @return
     */
    @GetMapping("/getMarket")
    MarketDto findByCoinId(@RequestParam("buyCoinId") Long buyCoinId, @RequestParam("sellCoinId") Long sellCoinId);

}
