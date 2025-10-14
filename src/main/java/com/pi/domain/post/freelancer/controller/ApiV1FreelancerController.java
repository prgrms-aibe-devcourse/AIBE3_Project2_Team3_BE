package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerWriteReqBody;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
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
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary
    ) {
        Page<Post> postPage;

        boolean hasFilter =
                (categoryId != null) ||
                        (regionId != null) ||
                        (skillIds != null && !skillIds.isEmpty()) ||
                        (minSalary != null) ||
                        (maxSalary != null);

        if (hasFilter) {
            postPage = freelancerService.search(pageable, categoryId, regionId, skillIds, searchKeyword, minSalary, maxSalary);
        } else {
            postPage = freelancerService.getPage(pageable, searchKeyword);
        }

        Page<FreelancerDto> dtoPage = postPage.map(FreelancerDto::new);
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
}

