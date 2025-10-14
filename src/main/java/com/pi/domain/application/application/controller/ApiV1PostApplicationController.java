package com.pi.domain.application.application.controller;

import com.pi.domain.application.application.dto.PostOwnerApplicationWithUserDto;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.service.ApplicationService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1PostApplicationController", description = "API 게시글 구직 컨트롤러")
public class ApiV1PostApplicationController {
    private final Rq rq;
    private final ApplicationService applicationService;
    private final ProjectService projectService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public PagePayload<PostOwnerApplicationWithUserDto> getItems(
            @PathVariable Long postId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(postId);
        post.checkActorCanReadApplication(actor);

        Page<PostOwnerApplicationWithUserDto> dtoPage = applicationService.findAllByPostIdAndStatus(postId, status, pageable)
                .map(application -> new PostOwnerApplicationWithUserDto(application, application.getUser()));

        return Ut.pageMapper.of(dtoPage);
    }
}
