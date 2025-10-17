package com.pi.domain.post.project.controller;

import com.pi.domain.post.post.entity.LikeResBody;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.entity.ViewResBody;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectDto;
import com.pi.domain.post.project.dto.ProjectModifyReqBody;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.post.project.dto.ProjectWriteReqBody;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.reaction.reaction.service.ReactionService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final ReactionService reactionService;
    private final Rq rq;

    @PostMapping
    @Operation(summary = "프로젝트 글 작성")
    public RsData<ProjectDto> write(
            @Valid @RequestBody ProjectWriteReqBody reqBody
    ) {
        User actor = rq.getActor();
        ProjectDto dto = projectService.create(actor, reqBody.post(), reqBody.project(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("201-1", "프로젝트 게시글이 등록되었습니다.", dto);
    }

    @GetMapping
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
        User actor = rq.getActorOrNull();
        Long userId = (actor != null) ? actor.getId() : null;
        Page<ProjectDto> dtoPage = projectService.searchProjects(new ProjectSearchParams(regionIds, categoryIds, skillIds, minSalary, maxSalary, keyword), pageable, userId);
        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/my")
    @Operation(summary = "내가 쓴 프로젝트 글 다건 조회")
    public PagePayload<ProjectDto> getMyItems(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        Page<ProjectDto> dtoPage = projectService.getMyProjects(actor, pageable);
        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 글 단건 조회")
    public ProjectDto getItem(
            @PathVariable Long id
    ) {
        User actor = rq.getActorOrNull();
        Long userId = (actor != null) ? actor.getId() : null;
        return projectService.getItem(id, userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "프로젝트 글 수정")
    public RsData<ProjectDto> modify(
            @PathVariable Long id,
            @Valid @RequestBody ProjectModifyReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(id);
        post.checkActorCanModify(actor);
        ProjectDto dto = projectService.modify(post, reqBody.post(), reqBody.project(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds(), actor.getId());

        return new RsData<>("200-1", "프로젝트 게시글이 수정되었습니다.", dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "프로젝트 글 삭제")
    public RsData<Void> delete(
            @PathVariable Long id
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(id);
        post.checkActorCanDelete(actor);
        postService.delete(post);

        return new RsData<>("200-1", "프로젝트 게시글이 삭제되었습니다.");
    }

    @PostMapping("/{id}/views")
    @Operation(summary = "프로젝트 글 조회수 증가")
    public RsData<ViewResBody> increaseViewCount(@PathVariable Long id) {
        long current = postService.increaseViewCount(id);
        return new RsData<>("200-1", "프로젝트 게시글 조회수가 증가되었습니다.", new ViewResBody(id, current));
    }

    @PostMapping("/{id}/likes")
    @Operation(summary = "좋아요 ON")
    public RsData<LikeResBody> likeOn(@PathVariable Long id) {
        User actor = rq.getActor();
        boolean on = reactionService.likeOn(actor, id);
        long count = reactionService.getLikeCount(id);
        return new RsData<>("200-1", on ? "좋아요 완료" : "이미 좋아요 상태",
                new LikeResBody(id, count, true));
    }

    @DeleteMapping("/{id}/likes")
    @Operation(summary = "좋아요 OFF")
    public RsData<LikeResBody> likeOff(@PathVariable Long id) {
        User actor = rq.getActor();
        boolean off = reactionService.likeOff(actor, id);
        long count = reactionService.getLikeCount(id);
        return new RsData<>("200-2", off ? "좋아요 해제" : "이미 해제 상태",
                new LikeResBody(id, count, false));
    }

    @PostMapping("/{id}/likes/toggle")
    @Operation(summary = "좋아요 토글")
    public RsData<LikeResBody> likeToggle(@PathVariable Long id) {
        User actor = rq.getActor();
        boolean on = reactionService.toggleLike(actor, id);
        long count = reactionService.getLikeCount(id);
        return new RsData<>("200-3", on ? "좋아요 ON" : "좋아요 OFF",
                new LikeResBody(id, count, on));
    }
}