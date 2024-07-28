package com.gnailcn.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "coin的RPC数据传输对象")
public class CoinDto {

    @ApiModelProperty(value = "币种的Id")
    private Long id;

    @ApiModelProperty(value = "币种的名称")
    private String name;

    @ApiModelProperty(value = "币种的标题")
    private String title;

    @ApiModelProperty(value = "币种的图标")
    private String img;

    @ApiModelProperty(value = "最小的提现单位")
    private BigDecimal baseAmount;

    @ApiModelProperty(value = "单笔最小提现数量")
    private BigDecimal minAmount;

    @ApiModelProperty(value = "单笔最大提现数量")
    private BigDecimal maxAmount;

    @ApiModelProperty(value = "每日最大提现数量")
    private BigDecimal dayMaxAmount;

}
