package com.pi.domain.review.review.controller;


import com.pi.domain.contract.contract.repository.ContractRepository;
import com.pi.domain.review.review.service.ReviewService;
import com.pi.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1ReviewControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("리뷰 작성")
    @WithUserDetails("user1")
    void t1() throws Exception {
//        Post post = postRepository.save(new Post("테스트 게시글", "테스트 내용", userRepository.findByUsername("user1").orElseThrow()));
//        User user1 = userRepository.findByUsername("user1").orElseThrow();
//        User user2 = userRepository.findByUsername("user2").orElseThrow(); // 상대방 유저
//        Contract contract = contractRepository.save(new Contract(post1, user1, user2,"IN_PROGRESS"));

        mvc.perform(post("/api/v1/reviews/{contractId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "rating": 5,
                        "comment": "좋은 경험이었습니다."
                    }
                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("리뷰 조회")
    @WithUserDetails("user1")
    void t2() throws Exception {
        mvc.perform(get("/api/v1/reviews/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.score").exists())
                .andExpect(jsonPath("$.targetUserId").exists());
    }

    @Test
    @DisplayName("리뷰 수정")
    @WithUserDetails("user1")
    void t3() throws Exception {
        mvc.perform(put("/api/v1/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "rating": 4,
                        "comment": "수정된 리뷰입니다."
                    }
                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.content").value("수정된 리뷰입니다."))
                .andExpect(jsonPath("$.data.score").value(4));
    }

    @Test
    @DisplayName("리뷰 삭제")
    @WithUserDetails("user1")
    void t4() throws Exception {
        mvc.perform(delete("/api/v1/reviews/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").exists());
    }
}