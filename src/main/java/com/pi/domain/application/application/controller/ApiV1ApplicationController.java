package com.pi.domain.application.application.controller;

import com.pi.domain.application.application.dto.*;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.service.ApplicationService;
import com.pi.domain.application.file.dto.ApplicationFileDto;
import com.pi.domain.application.file.service.ApplicationFileService;
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
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1ApplicationController", description = "API 구직 컨트롤러")
public class ApiV1ApplicationController {
    private static final String AWS_S3_DIRECTORY = "application";
    private final Rq rq;
    private final ApplicationService applicationService;
    private final ProjectService projectService;
    private final ApplicationFileService applicationFileService;
    private final AwsS3Service awsS3Service;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public PagePayload<ApplicationWithPostDto> getMyItems(
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        User actor = rq.getActor();
        Page<ApplicationWithPostDto> dtoPage = applicationService.findAllByUserIdAndStatus(actor.getId(), status, pageable)
                .map(application -> new ApplicationWithPostDto(application, application.getPost()));

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public ApplicationGetResBody getItem(@PathVariable Long id) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);
        User user = application.getUser();
        application.checkActorCanRead(actor, user);

        return new ApplicationGetResBody(
                application,
                application.getFiles().stream()
                        .map(file -> new ApplicationFileDto(
                                file.getId(),
                                file.getUrl(),
                                awsS3Service.getOriginalFileNameFromUrl(file.getUrl())
                        ))
                        .toList()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "등록 (임시저장 또는 제출)")
    public RsData<ApplicationWriteResBody> write(
            @Valid @RequestPart ApplicationWriteReqBody reqBody,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Post post = projectService.findById(reqBody.postId());

        User actor = rq.getActor();
        User PostUser = post.getUser();
        if (actor.getUsername().equals(PostUser.getUsername())) {
            throw new ServiceException("403-2", "본인이 등록한 게시글에는 지원할 수 없습니다.");
        }

        Application application = applicationService.createOrUpdate(post, actor, reqBody, files);

        return new RsData<>(
                "201-1",
                "%d번 구직이 등록되었습니다.".formatted(application.getId()),
                new ApplicationWriteResBody(application)
        );
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "상태 수정")
    public RsData<ApplicationModifyResBody> modifyStatus(
            @PathVariable long id,
            @Valid @RequestBody ApplicationModifyReqBody reqBody
    ) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);

        User user = application.getUser();
        application.checkActorCanModify(actor, user);

        applicationService.updateStatus(application, reqBody.status(), true);

        return new RsData<>("200-1",
                "%d번 구직 상태가 수정되었습니다.".formatted(id),
                new ApplicationModifyResBody(application.getStatus())
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        User actor = rq.getActor();
        Application application = applicationService.findById(id);
        application.checkActorCanDelete(actor);

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new ServiceException("400-1", "제출된 구직은 삭제할 수 없습니다.");
        }

        applicationService.delete(application);

        return new RsData<>("200-3",
                "%d번 구직이 삭제되었습니다.".formatted(id)
        );
    }
}
