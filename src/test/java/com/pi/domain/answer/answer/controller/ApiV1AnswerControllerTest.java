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
        testAnswer = answerRepository.findAll().get(0);
        testQuestion = questionRepository.findAll().get(0);
    }

    @AfterEach
    void tearDown() {
        // 필요시 후처리
    }

    @Test
    @DisplayName("관리자가 답변 작성")
    @WithUserDetails("admin")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/answers")
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
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/answers")
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
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/answers/" + testAnswer.getId())
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
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/answers/" + testAnswer.getId())
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
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/answers/" + testAnswer.getId())
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
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/answers/" + testAnswer.getId())
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("답변을 삭제할 권한이 없습니다."));
    }

}
