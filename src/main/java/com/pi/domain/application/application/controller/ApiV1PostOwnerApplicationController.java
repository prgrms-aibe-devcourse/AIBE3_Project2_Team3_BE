package com.pi.domain.application.application.controller;

import com.pi.domain.application.application.dto.ApplicationModifyResBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationGetResBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationModifyReqBody;
import com.pi.domain.application.application.dto.PostOwnerApplicationWithUserDto;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.service.ApplicationService;
import com.pi.domain.application.file.dto.ApplicationFileDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.service.ProjectService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.s3.AwsS3Service;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/post-owner/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1PostOwnerApplicationController", description = "게시글 작성자용 API 구직 컨트롤러")
public class ApiV1PostOwnerApplicationController {
    private final Rq rq;
    private final ApplicationService applicationService;
    private final ProjectService projectService;
    private final AwsS3Service awsS3Service;

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
        post.checkActorCanReadApplication(actor);

        Page<PostOwnerApplicationWithUserDto> dtoPage = applicationService.findAllByPostIdAndStatusForPostOwner(postId, status, pageable)
                .map(application -> new PostOwnerApplicationWithUserDto(application, application.getUser()));

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public PostOwnerApplicationGetResBody getItem(@PathVariable Long id) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);
        User postUser = application.getPost().getUser();
        application.checkActorCanRead(actor, postUser);

        return new PostOwnerApplicationGetResBody(
                application,
                application.getUser(),
                application.getFiles().stream()
                        .map(file -> new ApplicationFileDto(
                                file.getId(),
                                file.getUrl(),
                                awsS3Service.getOriginalFileNameFromUrl(file.getUrl())
                        ))
                        .toList()
        );
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "상태 수정")
    public RsData<ApplicationModifyResBody> modifyStatus(
            @PathVariable long id,
            @Valid @RequestBody PostOwnerApplicationModifyReqBody reqBody
    ) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);

        User user = application.getPost().getUser();
        application.checkActorCanModify(actor, user);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            log.warn("수락/거절된 구직({})은 수정 불가", application.getId());
            throw new ServiceException("400-1", "잘못된 요청입니다.");
        }
        applicationService.updateStatus(application, reqBody.status());

        return new RsData<>(
                "200-1",
                "%d번 구직 상태가 수정되었습니다.".formatted(id),
                new ApplicationModifyResBody(application.getStatus())
        );
    }
}
