package com.pi.global.initData;

import com.pi.domain.user.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner prodUserInitDataRunner() {
        return args -> {
            initUsers();
        };
    };

    @Transactional
    public void initUsers () {
        if (userRepository.count() > 5) {
            return;
        }



    }
}
