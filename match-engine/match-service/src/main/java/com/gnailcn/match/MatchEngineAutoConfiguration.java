package com.gnailcn.match;

import com.gnailcn.disruptor.OrderEvent;
import com.gnailcn.disruptor.OrderEventHandler;
import com.gnailcn.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(value = MatchEngineProperties.class)
public class MatchEngineAutoConfiguration {
    private MatchEngineProperties matchEngineProperties;


    public MatchEngineAutoConfiguration(MatchEngineProperties matchEngineProperties) {
        this.matchEngineProperties = matchEngineProperties;
    }


    @Bean("eventHandlers")
    public EventHandler<OrderEvent>[] eventHandlers() {
        //获取配置文件中的交易对字典
        Map<String, MatchEngineProperties.CoinScale> symbols = matchEngineProperties.getSymbols();
        //获取交易对的key-value
        Set<Map.Entry<String, MatchEngineProperties.CoinScale>> entries = symbols.entrySet();
        EventHandler<OrderEvent>[] eventHandlers = new EventHandler[symbols.size()];
        int i = 0;
        //遍历交易对
        for (Map.Entry<String, MatchEngineProperties.CoinScale> entry : entries) {
            String symbol = entry.getKey();
            MatchEngineProperties.CoinScale value = entry.getValue();
            OrderBooks orderBooks = null;
            if (value != null) {
                orderBooks = new OrderBooks(symbol, value.getCoinScale(), value.getBaseCoinScale());
            } else {
                orderBooks = new OrderBooks(symbol);
            }
            eventHandlers[i++] = new OrderEventHandler(orderBooks);
        }
        return eventHandlers;
    }
}
