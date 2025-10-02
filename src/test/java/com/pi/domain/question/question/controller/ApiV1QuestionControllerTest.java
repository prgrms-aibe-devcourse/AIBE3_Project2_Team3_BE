package com.pi.domain.question.question.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.domain.question.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1QuestionControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("질문 목록 조회")
    @WithMockUser(username = "user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/v1/questions"))
                .andDo(print());

        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("질문 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data").exists());
    }
}