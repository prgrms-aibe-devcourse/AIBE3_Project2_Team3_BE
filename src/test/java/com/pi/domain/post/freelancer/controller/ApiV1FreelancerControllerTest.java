package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.service.FreelancerService;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1FreelancerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private FreelancerService freelancerService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("프리랜서 글 작성")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/freelancers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "post": {
                                                "title": "프리랜서",
                                                "content": "만들어드립니다.",
                                                "isViewed": true
                                            },
                                            "freelancer": {
                                                "salary": 100,
                                                "period": 12
                                            },
                                            "regionIds": [1],
                                            "categoryIds": [1],
                                            "skillIds": [1]
                                        }
                                        """ )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("프리랜서 게시글이 등록되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("프리랜서"))
                .andExpect(jsonPath("$.data.content").value("만들어드립니다."))
                .andExpect(jsonPath("$.data.salary").value(100))
                .andExpect(jsonPath("$.data.period").value(12));
    }

    @Test
    @DisplayName("프리랜서 글 단건 조회")
    @WithUserDetails("user1")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/freelancers/1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("프리랜서"))
                .andExpect(jsonPath("$.content").value("만들어드립니다."))
                .andExpect(jsonPath("$.salary").value(100))
                .andExpect(jsonPath("$.period").value(12));
    }

    @Test
    @DisplayName("프리랜서 글 수정")
    @WithUserDetails("user1")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/freelancers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "post": {
                                                "title": "수정된 프리랜서",
                                                "content": "수정된 내용",
                                                "isViewed": false
                                            },
                                            "freelancer": {
                                                "salary": 200,
                                                "period": 24
                                            },
                                            "regionIds": [1],
                                            "categoryIds": [1],
                                            "skillIds": [1]
                                        }
                                        """ )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("프리랜서 게시글이 수정되었습니다."))
                .andExpect(jsonPath("$.data.title").value("수정된 프리랜서"))
                .andExpect(jsonPath("$.data.content").value("수정된 내용"))
                .andExpect(jsonPath("$.data.salary").value(200))
                .andExpect(jsonPath("$.data.period").value(24));
    }

    @Test
    @DisplayName("프리랜서 글 삭제")
    @WithUserDetails("user1")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/freelancers/1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("프리랜서 게시글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("프리랜서 글 필터 검색")
    @WithUserDetails("user1")
    void t5() throws Exception {
        // given: 필터 조건 (카테고리, 지역, 스킬, 급여범위, 키워드)
        String url = "/api/v1/freelancers?categoryIds=1&regionIds=1&skillIds=1&minSalary=50&maxSalary=200&keyword=프리랜서";

        // when
        ResultActions resultActions = mvc
                .perform(get(url))
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].title").value("프리랜서"))
                .andExpect(jsonPath("$.data.items[0].salary").value(100));
    }

    @Test
    @DisplayName("내가 작성한 프리랜서 글 조회 (Page 형식)")
    @WithUserDetails("user1")
    void t6() throws Exception {
        // given
        String url = "/api/v1/freelancers/my?page=0&size=5";

        // when
        ResultActions resultActions = mvc
                .perform(get(url))
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(com.pi.domain.post.freelancer.controller.ApiV1FreelancerController.class))
                .andExpect(handler().methodName("getMyFreelancers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].title").value("프리랜서"))
                .andExpect(jsonPath("$.data.items[0].salary").value(100));
    }

}
