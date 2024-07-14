package com.gnailcn.controller;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.gnailcn.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 完成文件上传的功能
 */
@RestController
@Api(tags = "文件上传的控制器")
public class FileController {


    @Autowired
    private OSS ossClient;  // spring-cloud-alibaba-oss 会自动的注入该对象，报红不要紧

    @Value("${oss.bucket.name:gnailcn-coin-exchange-imgs}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    @ApiOperation(value = "上传文件")
    @PostMapping("/image/AliYunImgUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "你要上传的文件")
    })
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        /**
         * 1 bucketName
         * 2 文件的名称 日期 +原始的文件名（uuid）来做
         * 3 文件的输入流
         */
        //////////////// 2020-10-10->2020/10/10/xxx.jpg
        String fileName = DateUtil.today().replaceAll("-", "/") + "/" + file.getOriginalFilename();
        ossClient.putObject(bucketName, fileName, file.getInputStream());
        // https://coinoss.free.idcfengye.com.oss-cn-beijing.aliyuncs.com/2020/10/10/xxx.jpg
        //https://coinoss.free.idcfengye.com.oss-cn-beijing.aliyuncs.com/2020/10/10/banner9.jpg
        return R.ok("https://" + bucketName + "." + endPoint + "/" + fileName); //能使用浏览器访问到文件路径http://xxx.com/路径
    }
}