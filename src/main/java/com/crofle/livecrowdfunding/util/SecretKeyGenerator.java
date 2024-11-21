package com.crofle.livecrowdfunding.util;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecretKeyGenerator {
    public static void main(String[] args) throws Exception {
        // 키 생성기 초기화 (HmacSHA256 알고리즘 사용)
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256); // 256비트 키 생성
        SecretKey secretKey = keyGen.generateKey();

        // 비밀 키를 Base64로 인코딩
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        // 생성된 비밀 키 출력
        System.out.println("Base64 Encoded Secret Key: " + encodedKey);
    }
}
