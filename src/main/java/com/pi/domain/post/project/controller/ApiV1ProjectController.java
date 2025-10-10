package com.pi.domain.post.project.controller;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectDto;
import com.pi.domain.post.project.dto.ProjectModifyReqBody;
import com.pi.domain.post.project.dto.ProjectResponse;
import com.pi.domain.post.project.dto.ProjectWriteReqBody;
import com.pi.domain.post.project.entity.ProjectStatus;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "ApiV1ProjectController", description = "API 프로젝트 컨트롤러")
public class ApiV1ProjectController {
    private final ProjectService projectService;
    private final PostService postService;
    private final Rq rq;

    //TODO: 현재 글 작성, 수정, 삭제, 단건 조회, 다건 조회 기능 +추가할것:
    @PostMapping
    @Transactional
    @Operation(summary = "프로젝트 글 작성")
    public RsData<ProjectDto> write(
            @Valid @RequestBody ProjectWriteReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = projectService.create(actor, reqBody.post(), reqBody.project(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("201-1", "%d번 프로젝트 게시글이 등록되었습니다.".formatted(post.getId()), new ProjectDto(post));
    }

    @GetMapping
    @Transactional
    @Operation(summary = "프로젝트 글 다건 조회")
    public PagePayload<ProjectDto> getItems(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "") String searchKeyword
    ) {
        Page<ProjectDto> dtoPage = projectService.getPage(pageable, searchKeyword).map(ProjectDto::new);
        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/projects")
    public List<ProjectResponse> getProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword
    ) {
        return projectService.searchProjects(status, region, keyword);
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "프로젝트 글 단건 조회")
    public RsData<ProjectDto> getItem(
            @PathVariable Long id
    ) {
        Post post = projectService.findById(id);
        return new RsData<>("200-1", "프로젝트 게시글이 조회되었습니다.", new ProjectDto(post));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "프로젝트 글 수정")
    public RsData<ProjectDto> modify(
            @PathVariable Long id,
            @Valid @RequestBody ProjectModifyReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(id);
        post.checkActorCanModify(actor);
        projectService.modify(post, reqBody.post(), reqBody.project(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("200-1", "%d번 프로젝트 게시글이 수정되었습니다.".formatted(post.getId()), new ProjectDto(post));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "프로젝트 글 삭제")
    public RsData<Void> delete(
            @PathVariable Long id
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(id);
        post.checkActorCanDelete(actor);
        postService.delete(post);

        return new RsData<>("200-1", "%d번 프로젝트 게시글이 삭제되었습니다.".formatted(post.getId()));
    }
}