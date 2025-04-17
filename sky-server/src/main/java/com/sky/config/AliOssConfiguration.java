package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliOssConfiguration {

    @Bean
    public AliOSSUtils aliOSSUtils(AliOssProperties aliOssProperties){
        AliOSSUtils aliOSSUtils = new AliOSSUtils(aliOssProperties.getEndpoint(),
                aliOssProperties.getBucketName(),
                aliOssProperties.getRegion());
        return aliOSSUtils;
    }
}
