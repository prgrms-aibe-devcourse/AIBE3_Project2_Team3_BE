package com.pi.domain.user.user.controller;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    UserService userService;

    @Test
    @DisplayName("정보조회")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/users/me")
                )
                .andDo(print());

        User user = userService.findByUsername("user1").get();

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%s님 정보입니다.".formatted(user.getNickname())))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.createdDate").value(Matchers.startsWith(user.getCreatedDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(user.getModifiedDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("회원수정")
    @WithUserDetails("user1")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nickname": "user",
                                          "email": "test@test.com"
                                        }
                                        """)
                )
                .andDo(print());

        User user = userService.findByUsername("user1").get();

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%s님 정보가 수정되었습니다.".formatted(user.getNickname())));
    }

    @Test
    @DisplayName("회원 탈퇴")
    @WithUserDetails("user1")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."));
    }
}
