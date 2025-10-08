package com.pi.domain.answer.answer.controller;

import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.answer.answer.repository.AnswerRepository;
import com.pi.domain.question.question.entity.Question;
import com.pi.domain.question.question.repository.QuestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class ApiV1AnswerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private Question testQuestion;
    private Answer testAnswer;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== 테스트 시작 ===");
        testAnswer = answerRepository.findAll().get(0);
        testQuestion = questionRepository.findAll().get(0);
        System.out.println("테스트 질문 ID: " + testQuestion.getId());
        System.out.println("테스트 답변 ID: " + testAnswer.getId());
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== 테스트 종료 ===\n");
    }

    @Test
    @DisplayName("관리자가 답변 작성")
    @WithUserDetails("admin")
    void t1() throws Exception {
        System.out.println("\n### 테스트 1: 관리자가 답변 작성 ###");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/answers/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "관리자 답변입니다.",
                                            "questionId": %d
                                        }
                                        """.formatted(testQuestion.getId()))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1AnswerController.class))
                .andExpect(handler().methodName("createAnswer"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("관리자가 답변을 등록했습니다."))
                .andExpect(jsonPath("$.data.content").value("관리자 답변입니다."));
    }

    @Test
    @DisplayName("일반 사용자는 답변 작성 불가")
    @WithUserDetails("user1")
    void t2() throws Exception {
        System.out.println("\n### 테스트 2: 일반 사용자 답변 작성 시도 ###");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/answers/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "일반 사용자 답변입니다.",
                                            "questionId": %d
                                        }
                                        """.formatted(testQuestion.getId()))
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("관리자만 답변을 작성할 수 있습니다."));
    }

    @Test
    @DisplayName("답변 수정")
    @WithUserDetails("admin")
    void t3() throws Exception {
        System.out.println("\n### 테스트 3: 관리자가 답변 수정 ###");
        System.out.println("수정할 답변 ID: " + testAnswer.getId());

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/answers/modify/" + testAnswer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "수정된 답변입니다."
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1AnswerController.class))
                .andExpect(handler().methodName("modifyAnswer"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("관리자가 %d번 답변을 수정했습니다.".formatted(testAnswer.getId())))
                .andExpect(jsonPath("$.data.content").value("수정된 답변입니다."));
    }

    @Test
    @DisplayName("답변 수정 - 권한 없음")
    @WithUserDetails("user1")
    void t4() throws Exception {
        System.out.println("\n### 테스트 4: 일반 사용자 답변 수정 시도 ###");
        System.out.println("수정 시도할 답변 ID: " + testAnswer.getId());

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/answers/modify/" + testAnswer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "수정된 답변입니다."
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("답변을 수정할 권한이 없습니다."));
    }

    @Test
    @DisplayName("답변 삭제")
    @WithUserDetails("admin")
    void t5() throws Exception {
        System.out.println("\n### 테스트 5: 관리자가 답변 삭제 ###");
        System.out.println("삭제할 답변 ID: " + testAnswer.getId());

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/answers/delete/" + testAnswer.getId())
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1AnswerController.class))
                .andExpect(handler().methodName("deleteAnswer"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("관리자가 %d번 답변을 삭제했습니다.".formatted(testAnswer.getId())));
    }

    @Test
    @DisplayName("답변 삭제 - 권한 없음")
    @WithUserDetails("user1")
    void t6() throws Exception {
        System.out.println("\n### 테스트 6: 일반 사용자 답변 삭제 시도 ###");
        System.out.println("삭제 시도할 답변 ID: " + testAnswer.getId());

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/answers/delete/" + testAnswer.getId())
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("답변을 삭제할 권한이 없습니다."));
    }

}
