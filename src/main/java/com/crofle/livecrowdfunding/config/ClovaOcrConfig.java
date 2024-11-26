package com.crofle.livecrowdfunding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "naver.clova.ocr")
@Getter
@Setter
public class ClovaOcrConfig {
    private String url;
    private String secret;
}
