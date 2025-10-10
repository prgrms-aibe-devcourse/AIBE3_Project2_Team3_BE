package com.pi.domain.application.application.controller;

import com.pi.domain.application.application.dto.ApplicationModifyReqBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationGetResBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationModifyResBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationWithUserDto;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.service.ApplicationService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post-owner/applications")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostOwnerApplicationController", description = "게시자용 API 구직 컨트롤러")
public class ApiV1PostOwnerApplicationController {
    private final Rq rq;
    private final ApplicationService applicationService;
    private final ProjectService projectService;

    @GetMapping("/post/{postId}")
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public PagePayload<PostOwnerApplicationWithUserDto> getItems(
            @PathVariable Long postId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(postId);
        if (!actor.getUsername().equals(post.getUser().getUsername())) {
            throw new ServiceException("403-1", "구직 조회 권한이 없습니다.");
        }

        Page<PostOwnerApplicationWithUserDto> dtoPage = applicationService.findAllByPostIdAndStatus(postId, status, pageable)
                .map(PostOwnerApplicationWithUserDto::new);

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public PostOwnerApplicationGetResBody getItem(@PathVariable Long id) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);
        User user = application.getPost().getUser();
        application.checkActorCanRead(actor, user);

        return new PostOwnerApplicationGetResBody(application);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "상태 수정")
    public RsData<PostOwnerApplicationModifyResBody> modifyStatus(
            @PathVariable long id,
            @Valid @RequestBody ApplicationModifyReqBody reqBody
    ) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);

        User user = application.getPost().getUser();
        application.checkActorCanModify(actor, user);

        applicationService.updateStatus(application, reqBody.status(), false);

        return new RsData<>(
                "200-2",
                "%d번 구직 상태가 수정되었습니다.".formatted(id),
                new PostOwnerApplicationModifyResBody(application.getStatus())
        );
    }
}
