package com.pi.global.initData;

import com.pi.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@RequiredArgsConstructor
@Configuration
public class DevInitData {
    @Value("${custom.frontend.src}")
    private String frontendSrc;

    @Profile("dev")
    @Bean
    ApplicationRunner devInitDataApplicationRunner() {
        return arg -> {
            Ut.cmd.runAsync(
                    "npx{{DOT_CMD}}",
                    "--yes",
                    "--package", "typescript",
                    "--package", "openapi-typescript",
                    "openapi-typescript", "http://localhost:8080/v3/api-docs/apiV1",
                    "-o", frontendSrc+"/global/backend/apiV1/schema.d.ts",
                    "--properties-required-by-default"
            );
        };
    }
}
