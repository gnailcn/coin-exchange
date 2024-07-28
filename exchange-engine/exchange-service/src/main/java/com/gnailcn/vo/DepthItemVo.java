package com.gnailcn.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class DepthItemVo {
    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private BigDecimal amount = BigDecimal.ZERO;
}
