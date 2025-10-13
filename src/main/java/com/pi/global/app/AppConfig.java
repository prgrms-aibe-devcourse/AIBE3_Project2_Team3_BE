package com.pi.global.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.global.util.Ut;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    private static ObjectMapper objectMapper;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    @PostConstruct // 빈(Bean)이 생성되고 의존성 주입이 끝난 직후에 딱 한 번 호출되는 초기화 훅(hook)
    public void postConstruct() {
        Ut.json.objectMapper = objectMapper;
    }
}
