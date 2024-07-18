package com.gnailcn.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户的身份认证信息")
public class UserAuthForm extends GeetestForm {
    @ApiModelProperty(value = "用户的真实姓名")
    @NotBlank
    private String realName;

    @ApiModelProperty(value = "用户的证件类型")
    @NotBlank
    private Integer idCardType;

    @ApiModelProperty(value = "用户的证件号码")
    @NotBlank
    private String idCard;

}
