package com.pi.domain.post.freelancer.controller;

import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyReqBody;
import com.pi.domain.post.freelancer.dto.FreelancerWriteReqBody;
import com.pi.domain.post.freelancer.entity.FreelancerFile;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.service.PostService;
import com.pi.domain.post.project.dto.ProjectSearchParams;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/freelancers")
@RequiredArgsConstructor
@Tag(name = "ApiV1FreelancerController", description = "API 프리랜서 컨트롤러")
public class ApiV1FreelancerController {
    private final FreelancerService freelancerService;
    private final PostService postService;
    private final Rq rq;

    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    @Operation(summary = "프리랜서 글 작성")
    public RsData<FreelancerDto> write(
            @Valid @RequestPart("data") FreelancerWriteReqBody reqBody,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.create(actor, reqBody.post(), reqBody.freelancer(),
                reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds(), files);

        List<FreelancerFile> fileList = freelancerService.getFilesByPost(post);
        return new RsData<>("200-1", "프리랜서 게시글이 등록되었습니다.", new FreelancerDto(post, fileList));
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


    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Transactional
    @Operation(summary = "프리랜서 글 수정")
    public RsData<FreelancerDto> modify(
            @PathVariable Long id,
            @Valid @RequestPart("data") FreelancerModifyReqBody reqBody,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.findById(id);
        post.checkActorCanModify(actor);

        Post modified = freelancerService.modify(post,
                reqBody.post(), reqBody.freelancer(),
                reqBody.regionIds(), reqBody.categoryIds(), reqBody.skillIds(), files);

        List<FreelancerFile> fileList = freelancerService.getFilesByPost(modified);
        return new RsData<>("200-2", "프리랜서 게시글이 수정되었습니다.", new FreelancerDto(modified, fileList));
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
        freelancerService.deleteAllFiles(post);
        postService.delete(post);

        return new RsData<>("200-1", "프리랜서 게시글이 삭제되었습니다.");
    }
}

