package com.gnailcn.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisruptorHandlerException  implements ExceptionHandler {


    @Override
    public void handleEventException(Throwable throwable, long sequence, Object event) {
        log.info("handleEventException Exception===>{} , sequence==> {} ,event===>{}",throwable.getMessage(), sequence, event);
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        log.info("OnStartHandler Exception===>{} ", throwable.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.info("OnShutdownHandler Exception===>{} ", throwable.getMessage());
    }
}
