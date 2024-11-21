package com.crofle.livecrowdfunding;

import com.crofle.livecrowdfunding.dto.response.ProjectResponseInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.service.AdminProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class})

public class LiveCrowdFundingBackendApplication {

	public static void main(String[] args) {SpringApplication.run(LiveCrowdFundingBackendApplication.class, args);}

}
