package com.pi.domain.notification.notification.controller;

import com.pi.domain.notification.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NotificationSSRControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    NotificationService notificationService;


    @Test
    @DisplayName("알림 목록 페이지")
    @WithUserDetails("user1")
    void t1() throws Exception {
        mvc.perform(
                        get("/notifications")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


}
