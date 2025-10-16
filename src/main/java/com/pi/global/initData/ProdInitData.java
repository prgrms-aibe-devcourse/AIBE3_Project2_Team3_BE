package com.pi.global.initData;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.entity.UserRole;
import com.pi.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdInitData {
    private final UserService userService;
    @Autowired
    @Lazy
    private ProdInitData self;

    @Bean
    ApplicationRunner prodInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (userService.count() > 0) return;

        User userSystem = userService.join("system", "1234", "시스템", "system@test.com", UserRole.ROLE_ADMIN);
        User userAdmin = userService.join("admin", "1234", "관리자", "admin@test.com", UserRole.ROLE_ADMIN);
        User user1 = userService.join("user1", "1234", "유저1", "user1@test.com");
        User user2 = userService.join("user2", "1234", "유저2", "user2@test.com");
        User user3 = userService.join("user3", "1234", "유저3", "user3@test.com");
    }
}
