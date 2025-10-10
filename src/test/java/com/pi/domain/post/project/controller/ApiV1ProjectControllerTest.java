package com.pi.domain.post.project.controller;

import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.skill.skill.repository.SkillRepository;
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
@Transactional
@AutoConfigureMockMvc
public class ApiV1ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SkillRepository skillRepository;


    @Test
    @DisplayName("프로젝트 등록")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc.perform(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        
                                        {
                                           "post": {
                                             "title": "프로젝트 제목",
                                             "content": "프로젝트 내용",
                                             "isViewed": true
                                           },
                                           "project": {
                                             "deadlineDate": "2025-10-10T12:00:00",
                                             "startedDate": "2025-10-11T12:00:00",
                                             "endedDate": "2025-12-31T12:00:00",
                                             "hirerType": "기업",
                                             "employmentType": "정규직",
                                             "salary": 5000000,
                                             "personnel": 3,
                                             "skillLevel": 2
                                           },
                                           "regionIds": [1,2],
                                           "categoryIds": [1],
                                           "skillIds": [1]
                                         }
                                        """)
                )
                .andDo(print());

        Post post = projectService.findLatestPost(); // 서비스에 이 메서드 있다고 가정

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("%d번 프로젝트 게시글이 등록되었습니다.".formatted(post.getId())))
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.data.salary").value(5000000))
                .andExpect(jsonPath("$.data.regions[0].name").value("서울"))
                .andExpect(jsonPath("$.data.regions[1].name").value("경기"))
                .andExpect(jsonPath("$.data.categories[0].name").value("웹 개발"))
                .andExpect(jsonPath("$.data.skills[0].name").value("Java"));
    }

    @Test
    @DisplayName("프로젝트 등록 - 제목 필수 400-1")
    @WithUserDetails("user1")
    void t1_1() throws Exception {
        ResultActions resultActions = mvc.perform(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                           "post": {
                                             "title": "",
                                             "content": "프로젝트 내용",
                                             "isViewed": true
                                           },
                                           "project": {
                                             "deadlineDate": "2025-10-10T12:00:00",
                                             "startedDate": "2025-10-11T12:00:00",
                                             "endedDate": "2025-12-31T12:00:00",
                                             "hirerType": "기업",
                                             "employmentType": "정규직",
                                             "salary": 5000000,
                                             "personnel": 3,
                                             "skillLevel": 2
                                           },
                                           "regionIds": [1,2],
                                           "categoryIds": [1],
                                           "skillIds": [1]
                                         }
                                        """)
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.message").value("post.title-NotBlank-must not be blank"));
    }

    @Test
    @DisplayName("프로젝트 단건 조회")
    @WithUserDetails("user1")
    void t2() throws Exception {
        long projectId = 2L;

        ResultActions resultActions = mvc.perform(
                        get("/api/v1/projects/%d".formatted(projectId))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("프로젝트"));
    }

    @Test
    @DisplayName("프로젝트 다건 조회")
    @WithUserDetails("user1")
    void t2_1() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/projects")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트 수정")
    @WithUserDetails("user1")
    void t3() throws Exception {
        long projectId = 2L;

        ResultActions resultActions = mvc.perform(
                        put("/api/v1/projects/%d".formatted(projectId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "post": {
                                                "title": "수정된 프로젝트 제목",
                                                "content": "수정된 프로젝트 내용",
                                                "isViewed": true
                                            },
                                            "project": {
                                                "deadlineDate": "2025-11-10T00:00:00",
                                                "startedDate": "2025-11-12T00:00:00",
                                                "endedDate": "2026-01-01T00:00:00",
                                                "hirerType": "기업",
                                                "employmentType": "정규직",
                                                "salary": 6000000,
                                                "personnel": 4,
                                                "skillLevel": 3
                                            },
                                           "regionIds": [1,2],
                                           "categoryIds": [1],
                                           "skillIds": [1]
                                        }
                                        """)
                )
                .andDo(print());

        Post post = projectService.findById(projectId);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%d번 프로젝트 게시글이 수정되었습니다.".formatted(post.getId())))
                .andExpect(jsonPath("$.data.title").value("수정된 프로젝트 제목"))
                .andExpect(jsonPath("$.data.salary").value(6000000));
    }

    @Test
    @DisplayName("프로젝트 수정 - 권한 없음 403-1")
    @WithUserDetails("user2")
    void t3_1() throws Exception {
        long projectId = 2L;
        ResultActions resultActions = mvc.perform(
                        put("/api/v1/projects/%d".formatted(projectId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "post": {
                                                "title": "수정된 프로젝트 제목",
                                                "content": "수정된 프로젝트 내용",
                                                "isViewed": true
                                            },
                                            "project": {
                                                "deadlineDate": "2025-11-10T00:00:00",
                                                "startedDate": "2025-11-12T00:00:00",
                                                "endedDate": "2026-01-01T00:00:00",
                                                "hirerType": "기업",
                                                "employmentType": "정규직",
                                                "salary": 6000000,
                                                "personnel": 4,
                                                "skillLevel": 3
                                            },
                                           "regionIds": [1,2],
                                           "categoryIds": [1],
                                           "skillIds": [1]
                                        }
                                        """)
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("프로젝트 삭제")
    @WithUserDetails("user1")
    void t4() throws Exception {
        long projectId = 2L;

        ResultActions resultActions = mvc.perform(
                        delete("/api/v1/projects/%d".formatted(projectId))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%d번 프로젝트 게시글이 삭제되었습니다.".formatted(projectId)));
    }

    @Test
    @DisplayName("프로젝트 삭제 - 권한 없음 403-1")
    @WithUserDetails("user2")
    void t4_1() throws Exception {
        long projectId = 2L;

        ResultActions resultActions = mvc.perform(
                        delete("/api/v1/projects/%d".formatted(projectId))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("프로젝트 삭제 - 없는 글 404-1")
    @WithUserDetails("user1")
    void t4_2() throws Exception {
        long projectId = 9999L;
        ResultActions resultActions = mvc.perform(
                        delete("/api/v1/projects/%d".formatted(projectId))
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 데이터입니다."));
    }

    @Test
    @DisplayName("프로젝트 검색")
    @WithUserDetails("user1")
    void t5() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/projects/projects")
                                .param("status", "모집중")
                                .param("region", "서울")
                                .param("keyword", "프로젝트")
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProjectController.class))
                .andExpect(handler().methodName("getProjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("프로젝트"))
                .andExpect(jsonPath("$[0].regions[0].name").value("서울"));
    }

    @Test
    @DisplayName("프로젝트 상태 변경 ")
    @WithUserDetails("user1")
    void t6() throws Exception {
        long projectId = 2L;
        mvc.perform(
                        patch("/api/v1/projects/{id}/status", projectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        { "status": "모집중" }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("모집중"));
    }
}