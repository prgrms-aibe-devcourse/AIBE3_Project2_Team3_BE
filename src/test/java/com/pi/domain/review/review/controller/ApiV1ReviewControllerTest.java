package com.pi.domain.review.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.domain.contract.contract.entity.Contract;
import com.pi.domain.contract.contract.entity.ContractStatus;
import com.pi.domain.contract.contract.repository.ContractRepository;
import com.pi.domain.review.review.dto.ReviewReqBody;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.rq.Rq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration",
        "custom.jwt.secretKey=test-secret-key"
})
@AutoConfigureMockMvc
@Transactional
class ApiV1ReviewControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser;          // 로그인할 유저
    private Contract baseContract;  // 기본 계약(리뷰 없음)
    private Rq rq;

    /** 메일 의존성 막기용 더미 Bean */
    @TestConfiguration
    static class DummyMailConfig {
        @Bean
        public JavaMailSender javaMailSender() {
            return new JavaMailSenderImpl();
        }
    }

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        contractRepository.deleteAll();
        userRepository.flush();

        // 로그인에 사용할 유저 준비
        testUser = userRepository.findByUsername("user1")
                .orElseGet(() -> userRepository.save(
                        new User("user1", passwordEncoder.encode("1234"), "유저1", "test@test.com")
                ));

        // 리뷰 없는 기본 계약 하나 생성
        baseContract = contractRepository.save(
                new Contract(null, testUser, testUser, ContractStatus.IN_PROGRESS)
        );
    }

    // [1] 리뷰 작성 성공: 리뷰 없는 새 계약에 작성
    @Test
    @DisplayName("리뷰 작성 성공")
    @WithUserDetails("user1")
    void t1_createReviewSuccess() throws Exception {
        Contract newContract = contractRepository.save(
                new Contract(null, testUser, testUser, ContractStatus.IN_PROGRESS)
        );

        ReviewReqBody reqBody = new ReviewReqBody(5, "정말 좋은 계약이었습니다.");

        ResultActions result = mvc.perform(
                post("/api/v1/reviews/{contractId}", newContract.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody))
        ).andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("리뷰가 작성되었습니다."));
    }

    // [2] 리뷰 중복 작성 실패: 같은 새 계약에 2번 작성
    @Test
    @DisplayName("리뷰 중복 작성 실패 - 이미 존재하는 계약")
    @WithUserDetails("user1")
    void t2_createReviewDuplicateFail() throws Exception {
        Contract newContract = contractRepository.save(
                new Contract(null, testUser, testUser, ContractStatus.IN_PROGRESS)
        );
        ReviewReqBody reqBody = new ReviewReqBody(5, "중복 테스트");

        // 첫 번째 작성: 200
        mvc.perform(
                post("/api/v1/reviews/{contractId}", newContract.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody))
        ).andExpect(status().isOk());

        // 두 번째 작성(중복): 409
        mvc.perform(
                        post("/api/v1/reviews/{contractId}", newContract.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqBody))
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.message").value("이미 이 계약에 대한 리뷰가 존재합니다."));
    }

    // [3] 리뷰 수정: 테스트 내에서 리뷰를 먼저 만든 뒤 수정
    @Test
    @DisplayName("리뷰 수정 성공")
    @WithUserDetails("user1")
    void t3_modifyReview() throws Exception {
        User writer = userRepository.findByUsername("user1").orElseThrow();

        Review saved = reviewRepository.save(
                new Review(baseContract, writer, 4, "초기 리뷰 내용")
        );

        ReviewReqBody reqBody = new ReviewReqBody(3, "수정된 리뷰입니다.");

        mvc.perform(
                        put("/api/v1/reviews/{reviewId}", saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqBody))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.message").value("리뷰가 수정되었습니다."));
    }

    // [4] 리뷰 삭제: 테스트 내에서 리뷰를 먼저 만든 뒤 삭제
    @Test
    @DisplayName("리뷰 삭제 성공")
    @WithUserDetails("user1")
    void t4_deleteReview() throws Exception {
        // ✅ rq에서 현재 로그인된 유저(세션에 올라온 객체)를 꺼냄
        User actor = rq.getActor();

        // ✅ 동일 세션의 actor를 리뷰 작성자로 지정
        Review review = reviewRepository.save(new Review(baseContract, actor, 5, "삭제 테스트용 리뷰"));

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/reviews/{id}", review.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-3"))
                .andExpect(jsonPath("$.message").value("리뷰가 삭제되었습니다."));
    }



    // [5] 리뷰 단건 조회: 테스트 내에서 리뷰 생성 후 조회
    @Test
    @DisplayName("리뷰 단건 조회 성공")
    @WithUserDetails("user1")
    void t5_getOneReview() throws Exception {
        Review saved = reviewRepository.save(
                new Review(baseContract, testUser, 4, "조회용 리뷰")
        );

        mvc.perform(get("/api/v1/reviews/{reviewId}", saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-4"))
                .andExpect(jsonPath("$.message").value("리뷰 조회 성공"))
                .andExpect(jsonPath("$.data.comment").value("조회용 리뷰"));
    }
}
