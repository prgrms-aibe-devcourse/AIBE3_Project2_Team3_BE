package com.pi.domain.post.project.controller;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectDto;
import com.pi.domain.post.project.dto.ProjectModifyReqBody;
import com.pi.domain.post.project.dto.ProjectSearchParams;
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
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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

    @PostMapping
    @Transactional
    @Operation(summary = "프로젝트 글 작성")
    public RsData<ProjectDto> write(
            @Valid @RequestBody ProjectWriteReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = projectService.create(actor, reqBody.post(), reqBody.project(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("201-1", "프로젝트 게시글이 등록되었습니다.".formatted(post.getId()), new ProjectDto(post));
    }

    @GetMapping
    @Transactional
    @Operation(summary = "프로젝트 글 다건 조회")
    public PagePayload<ProjectDto> getItems(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary
    ) {
        Page<ProjectDto> dtoPage = projectService.searchProjects(new ProjectSearchParams(regionIds, categoryIds, skillIds, minSalary, maxSalary, keyword), pageable);
        return Ut.pageMapper.of(dtoPage);
    }


    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "프로젝트 글 단건 조회")
    public ProjectDto getItem(
            @PathVariable Long id
    ) {
        Post post = projectService.findById(id);
        return new ProjectDto(post);
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

        return new RsData<>("200-1", "프로젝트 게시글이 수정되었습니다.".formatted(post.getId()), new ProjectDto(post));
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

        return new RsData<>("200-1", "프로젝트 게시글이 삭제되었습니다.".formatted(post.getId()));
    }

    @PatchMapping("/{id}/status")
    @Transactional
    @Operation(summary = "프로젝트 상태 변경")
    public RsData<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String status
    ) throws NotFoundException {
        projectService.changeStatus(id, ProjectStatus.fromDisplayValue(status));
        return new RsData<>("200-1", "프로젝트 상태가 변경되었습니다.");
    }

    @GetMapping("/me")
    @Transactional
    @Operation(summary = "내가 쓴 프로젝트 글 다건 조회")
    public PagePayload<ProjectDto> getMyItems(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        Page<ProjectDto> dtoPage = projectService.getMyProjects(actor, pageable);
        return Ut.pageMapper.of(dtoPage);
    }
}