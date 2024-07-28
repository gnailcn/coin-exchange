package com.gnailcn.rocket;

import com.gnailcn.disruptor.DisruptorTemplate;
import com.gnailcn.domain.EntrustOrder;
import com.gnailcn.model.Order;
import com.gnailcn.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumerListener {
    @Autowired
    private DisruptorTemplate disruptorTemplate;

    @StreamListener("order_in")  // 监听从order_in通道接收的消息
    public void handleMessage(EntrustOrder entrustOrder) {
        Order order = BeanUtils.entrustOrder2Order(entrustOrder);
        log.info("接收到了委托单:{}", order);
        disruptorTemplate.onData(order);
    }
}
