package com.crofle.livecrowdfunding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "business.verification")
@Getter
@Setter
public class BusinessVerificationConfig {
    private String url;
    private String apiKey;
}
