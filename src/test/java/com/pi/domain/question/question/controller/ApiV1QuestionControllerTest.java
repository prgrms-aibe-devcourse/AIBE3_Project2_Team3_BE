package com.pi.domain.question.question.controller;

import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.security.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class ApiV1QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private Question testQuestion;

    @BeforeEach
    void setUp() {
        System.out.println("=== 테스트 시작 ===");
        testQuestion = questionRepository.findAll().get(0);
        System.out.println("테스트 질문 ID: " + testQuestion.getId());
        System.out.println("질문 작성자 ID: " + testQuestion.getUser().getId());
        System.out.println("질문 작성자 username: " + testQuestion.getUser().getUsername());
    }



    @Test
    @DisplayName("일반 사용자가 질문 작성")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "테스트 제목",
                                            "content": "테스트 내용"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1QuestionController.class))
                .andExpect(handler().methodName("createQuestion"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.content").value("테스트 내용"));
    }

    @Test
    @DisplayName("관리자는 질문 작성 불가")
    @WithUserDetails("admin")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "관리자 질문",
                                            "content": "관리자 내용"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"));
    }

    @Test
    @DisplayName("일반 사용자가 자신의 질문 수정")
    @WithUserDetails("user1")
    void t3() throws Exception {
        System.out.println("### 테스트: 자신의 질문 수정 ###");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        System.out.println("=== 현재 테스트 사용자 정보 ===");
        System.out.println("현재 사용자 ID: " + securityUser.getId());
        System.out.println("현재 사용자 username: " + securityUser.getUsername());



        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/questions/" + testQuestion.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "수정된 제목입니다",
                                            "content": "수정된 내용입니다"
                                        }
                                        """)
                )
                .andDo(result -> {
                    System.out.println("응답 결과: " + result.getResponse().getContentAsString());
                });

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1QuestionController.class))
                .andExpect(handler().methodName("modifyQuestion"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.title").value("수정된 제목입니다"))
                .andExpect(jsonPath("$.data.content").value("수정된 내용입니다"));
    }

    @Test
    @DisplayName("일반 사용자가 다른 사용자의 질문 수정 시도")
    @WithUserDetails("user2")
    void t4() throws Exception {
        System.out.println("### 테스트: 다른 사용자의 질문 수정 시도 ###");

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/questions/" + testQuestion.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "수정된 제목입니다",
                                            "content": "수정된 내용입니다"
                                        }
                                        """)
                )
                .andDo(result -> {
                    System.out.println("응답 결과: " + result.getResponse().getContentAsString());
                });

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("질문을 수정할 권한이 없습니다."));
    }

    @Test
    @DisplayName("일반 사용자가 자신의 질문 삭제")
    @WithUserDetails("user1")
    void t5() throws Exception {
        System.out.println("### 테스트: 자신의 질문 삭제 ###");

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/questions/" + testQuestion.getId())
                )
                .andDo(result -> {
                    System.out.println("응답 결과: " + result.getResponse().getContentAsString());
                });

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1QuestionController.class))
                .andExpect(handler().methodName("deleteQuestion"))
                .andExpect(jsonPath("$.resultCode").value("200-1"));
    }

    @Test
    @DisplayName("일반 사용자가 다른 사용자의 질문 삭제 시도")
    @WithUserDetails("user2")
    void t6() throws Exception {
        System.out.println("### 테스트: 다른 사용자의 질문 삭제 시도 ###");

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/questions/" + testQuestion.getId())
                )
                .andDo(result -> {
                    System.out.println("응답 결과: " + result.getResponse().getContentAsString());
                });

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("질문을 삭제할 권한이 없습니다."));
    }

    @Test
    @DisplayName("일반 사용자가 자신의 문의 및 답변 목록 조회")
    @WithUserDetails("user1")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/questions/my")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(result -> {
                    System.out.println("응답 결과: " + result.getResponse().getContentAsString());
                });

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1QuestionController.class))
                .andExpect(handler().methodName("getMyQuestionsWithAnswers"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}