package com.pi.global.initData;

import com.pi.domain.user.user.entity.UserRole;
import com.pi.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdUserInitData {
    private final UserService userService;

    @Bean
    ApplicationRunner prodUserInitDataRunner() {
        return args -> {
            initUsers();
        };
    }

    @Transactional
    public void initUsers() {
        if (userService.count() > 1) {
            return;
        }

        userService.join("admin", "admin123!", "관리자", "admin@pi.com", UserRole.ROLE_ADMIN);
        userService.join("system", "system123!", "시스템", "system@pi.com", UserRole.ROLE_ADMIN);
        userService.join("user1", "user123!", "유저1", "user1@pi.com");
        userService.join("user2", "user123!", "유저2", "user2@pi.com");
        userService.join("user3", "user123!", "유저3", "user34@pi.com");
    }
}
