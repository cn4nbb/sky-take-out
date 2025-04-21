package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/admin/common")
@RestController
@Slf4j
public class CommonController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传，参数为：{}",file);

        String url = null;

        try {
            url = aliOSSUtils.upload(file);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败，{}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
