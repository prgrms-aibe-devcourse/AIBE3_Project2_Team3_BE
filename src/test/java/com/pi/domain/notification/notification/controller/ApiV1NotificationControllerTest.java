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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ApiV1NotificationControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    NotificationService notificationService;

    @Test
    @DisplayName("알림 다건 조회")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/notifications")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1NotificationController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("내용1"));
    }

    @Test
    @DisplayName("알림 삭제")
    @WithUserDetails("user1")
    void t2() throws Exception {
        ResultActions resultActions = mvc.perform(
                        delete("/api/v1/notifications/1")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1NotificationController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("200-1"))
                .andExpect(jsonPath("message").value("알림이 삭제되었습니다."));

    }

    @Test
    @DisplayName("알림 구독")
    @WithUserDetails("user1")
    void t3() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/notifications/subscribe")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1NotificationController.class))
                .andExpect(handler().methodName("subscribe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/event-stream"));
    }

    @Test
    @DisplayName("알림 전송")
    @WithUserDetails("user1")
    void t4() throws Exception {
        ResultActions resultActions = mvc.perform(
                        post("/api/v1/notifications/send")
                                .param("message", "테스트")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1NotificationController.class))
                .andExpect(handler().methodName("send"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("200-1"))
                .andExpect(jsonPath("message").value("알림이 전송되었습니다."));
    }
}
