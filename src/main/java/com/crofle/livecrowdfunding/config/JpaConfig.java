package com.crofle.livecrowdfunding.config;

import com.crofle.livecrowdfunding.repository.redis.AccountViewRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.crofle.livecrowdfunding.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA 설정
}
