package com.pi.domain.review.review.controller;


import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private com.pi.global.s3.AwsS3Service awsS3Service;

    @MockBean
    private com.pi.global.s3.AwsS3Config awsS3Config;

    private Long savedReviewId;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByUsername("user1").orElseThrow();
        Post post = postRepository.findById(1L).orElseThrow();
        Review review = reviewRepository.save(new Review(post, user, 5, "테스트용 리뷰"));
        savedReviewId = review.getId();

        if (postRepository.findById(2L).isEmpty()) {
            postRepository.save(new Post(user, "작성용 제목", "작성용 내용"));
        }
    }

    @Test
    @DisplayName("리뷰 작성")
    @WithUserDetails("user1")
    void t1() throws Exception {
        mvc.perform(post("/api/v1/reviews/{postId}", 2)
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
    @DisplayName("리뷰 단건 조회")
    @WithUserDetails("user1")
    void t2() throws Exception {
        mvc.perform(get("/api/v1/reviews/{reviewId}", savedReviewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-4"))
                .andExpect(jsonPath("$.message").value("리뷰 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(savedReviewId.intValue()))
                .andExpect(jsonPath("$.data.comment").exists())
                .andExpect(jsonPath("$.data.rating").exists())
                .andExpect(jsonPath("$.data.userNickname").exists());
    }

    @Test
    @DisplayName("리뷰 수정")
    @WithUserDetails("user1")
    void t3() throws Exception {
        mvc.perform(put("/api/v1/reviews/{reviewId}", savedReviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "rating": 4,
                                "comment": "수정된 리뷰입니다."
                            }
                        """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.message").value("리뷰가 수정되었습니다."))
                .andExpect(jsonPath("$.data.comment").value("수정된 리뷰입니다."))
                .andExpect(jsonPath("$.data.rating").value(4));
    }

    @Test
    @DisplayName("리뷰 삭제")
    @WithUserDetails("user1")
    void t4() throws Exception {
        mvc.perform(delete("/api/v1/reviews/{reviewId}", savedReviewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-3"))
                .andExpect(jsonPath("$.message").value("리뷰가 삭제되었습니다."));
    }

    @Test
    @DisplayName("프리랜서 리뷰 전체 조회")
    @WithUserDetails("user1")
    void t5() throws Exception {
        mvc.perform(get("/api/v1/reviews/freelancer/{freelancerId}", 1)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-5"))
                .andExpect(jsonPath("$.message").value("프리랜서 리뷰 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("프로젝트 리뷰 전체 조회")
    @WithUserDetails("user1")
    void t6() throws Exception {
        mvc.perform(get("/api/v1/reviews/project/{projectId}", 1)
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-6"))
                .andExpect(jsonPath("$.message").value("프로젝트 리뷰 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}