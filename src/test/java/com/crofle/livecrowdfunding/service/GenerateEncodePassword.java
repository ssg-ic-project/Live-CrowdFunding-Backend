package com.crofle.livecrowdfunding.service;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//매니저는 회원등록이 없기 때문에 더미에 대한 비번을 임의로 생성해 줄 것임
@SpringBootTest
public class GenerateEncodePassword {

    @Test
    public void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "MgrPass3!!!";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("비밀번호: " + encodedPassword);
    }
}
