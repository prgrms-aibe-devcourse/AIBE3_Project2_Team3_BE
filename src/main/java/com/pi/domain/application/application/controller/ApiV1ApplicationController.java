package com.pi.domain.application.application.controller;

import com.pi.domain.application.application.dto.*;
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
import com.pi.global.s3.S3KeyParser;
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
    private final Rq rq;
    private final ApplicationService applicationService;
    private final ProjectService projectService;
    private final S3KeyParser s3KeyParser;

    @GetMapping("/my")
    @Transactional(readOnly = true)
    @Operation(summary = "내가 등록한 구직 다건 조회")
    public PagePayload<ApplicationWithUserDto> getMyItems(
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        User actor = rq.getActor();
        Page<ApplicationWithUserDto> dtoPage = applicationService.findAllByUserIdAndStatus(actor.getId(), status, pageable)
                .map(application -> new ApplicationWithUserDto(application, application.getPost(), application.getPost().getUser()));

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/received")
    @Transactional(readOnly = true)
    @Operation(summary = "내 게시글에 들어온 구직 다건 조회")
    public PagePayload<ApplicationWithUserDto> getItems(
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ApplicationStatus status
    ) {
        User actor = rq.getActor();
        Page<ApplicationWithUserDto> dtoPage = applicationService.findAllByPostUserIdAndStatus(actor.getId(), status, pageable)
                .map(application -> new ApplicationWithUserDto(application, application.getPost(), application.getUser()));

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public ApplicationDto getItem(@PathVariable Long id) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);
        User user = application.getUser();
        User postUser = application.getPost().getUser();
        if (application.isDifferentUser(actor, user) && application.isDifferentUser(actor, postUser)) {
            log.warn("본인 또는 게시글 작성자만 조회 가능");
            throw new ServiceException("403-1", "권한이 없습니다.");
        }

        return new ApplicationDto(
                application,
                application.getFiles().stream()
                        .map(file -> new ApplicationFileDto(
                                file.getId(),
                                file.getUrl(),
                                s3KeyParser.getDecodedFileName(file.getUrl())
                        ))
                        .toList()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "등록")
    public RsData<ApplicationWriteResBody> write(
            @Valid @RequestPart ApplicationWriteReqBody reqBody,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        User actor = rq.getActor();
        Post post = projectService.findById(reqBody.postId());
        post.checkActorIsNotOwner(actor);

        Application application = applicationService.create(post, actor, reqBody, files);

        return new RsData<>(
                "201-1",
                "%d번 구직이 등록되었습니다.".formatted(application.getId()),
                new ApplicationWriteResBody(application)
        );
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "수정")
    public RsData<ApplicationModifyResBody> modify(
            @PathVariable long id,
            @Valid @RequestPart ApplicationModifyReqBody reqBody,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "removeIds", required = false) List<Long> removeIds
    ) {
        User actor = rq.getActor();
        Application application = applicationService.findById(id);

        User user = application.getUser();
        application.checkActorCanModify(actor, user);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            log.warn("수락/거절/완료된 구직({})은 수정 불가", application.getId());
            throw new ServiceException("400-1", "잘못된 요청입니다.");
        }
        applicationService.update(application, reqBody, files, removeIds);

        return new RsData<>("200-1",
                "%d번 구직이 수정되었습니다.".formatted(id),
                new ApplicationModifyResBody(application)
        );
    }

    @PutMapping("/{id}/status")
    @Transactional
    @Operation(summary = "상태 수정")
    public RsData<ApplicationModifyStatusResBody> modifyStatus(
            @PathVariable long id,
            @Valid @RequestBody ApplicationModifyStatusReqBody reqBody
    ) {
        User actor = rq.getActor();

        Application application = applicationService.findById(id);

        User postUser = application.getPost().getUser();
        application.checkActorCanModify(actor, postUser);

        applicationService.updateStatus(application, reqBody.status());

        return new RsData<>(
                "200-1",
                "%d번 구직 상태가 수정되었습니다.".formatted(id),
                new ApplicationModifyStatusResBody(application.getStatus().name())
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        User actor = rq.getActor();
        Application application = applicationService.findById(id);
        application.checkActorCanDelete(actor);

        applicationService.delete(application);

        return new RsData<>("200-1",
                "%d번 구직이 삭제되었습니다.".formatted(id)
        );
    }
}
