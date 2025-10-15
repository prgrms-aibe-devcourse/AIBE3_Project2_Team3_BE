package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerWriteReqBody;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.reaction.reaction.service.ReactionService;
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
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
@Tag(name = "ApiV1FreelancerController", description = "API 프리랜서 컨트롤러")
public class ApiV1FreelancerController {
    private final FreelancerService freelancerService;
    private final PostService postService;
    private final ReactionService reactionService;
    private final Rq rq;

    @PostMapping
    @Transactional
    @Operation(summary = "프리랜서 글 작성")
    public RsData<FreelancerDto> write(
            @Valid @RequestBody FreelancerWriteReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.create(actor, reqBody.post(), reqBody.freelancer(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("200-1", "프리랜서 게시글이 등록되었습니다.", new FreelancerDto(post));
    }

    @GetMapping
    @Transactional
    @Operation(summary = "프리랜서 글 다건 조회 (필터 + 검색 자동 분기)")
    public PagePayload<FreelancerDto> getItems(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> regionIds,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary
    ) {
        Page<FreelancerDto> dtoPage = freelancerService.searchFreelancers(new ProjectSearchParams(regionIds, categoryIds, skillIds, minSalary, maxSalary, keyword), pageable);
        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "프리랜서 글 단건 조회")
    public FreelancerDto getItem(
            @PathVariable Long id
    ) {
        Post post = freelancerService.findById(id);
        return new FreelancerDto(post);
    }

    @GetMapping("/my")
    @Transactional
    @Operation(summary = "내가 작성한 프리랜서 글 조회")
    public PagePayload<FreelancerDto> getMyFreelancers(
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        var page = freelancerService.getMyFreelancers(actor, pageable);
        return Ut.pageMapper.of(page);
    }


    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "프리랜서 글 수정")
    public RsData<FreelancerDto> modify(
            @PathVariable Long id,
            @Valid @RequestBody FreelancerModifyReqBody reqBody
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.findById(id);
        post.checkActorCanModify(actor);
        freelancerService.modify(post, reqBody.post(), reqBody.freelancer(), reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds());

        return new RsData<>("200-1", "프리랜서 게시글이 수정되었습니다.", new FreelancerDto(post));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "프리랜서 글 삭제")
    public RsData<Void> delete(
            @PathVariable Long id
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.findById(id);
        post.checkActorCanDelete(actor);
        postService.delete(post);

        return new RsData<>("200-1", "프리랜서 게시글이 삭제되었습니다.");
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "프리랜서 글 좋아요/취소")
    public RsData<Void> toggleLike(
            @PathVariable Long id) {
        User actor = rq.getActor();
        Long userId = actor.getId();
        reactionService.toggleLike(id, userId);
        return new RsData<>("200-1", "프로젝트 게시글 좋아요 상태가 변경되었습니다.");
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "프리랜서 글 조회수 증가")
    public RsData<Void> increaseViewCount(@PathVariable Long id) {
        postService.increaseViewCount(id);
        return new RsData<>("200-1", "프리랜서 게시글 조회수가 증가되었습니다.");
    }

}

