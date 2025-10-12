package com.pi.domain.application.application.service;

import com.pi.domain.application.application.dto.ApplicationWriteReqBody;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.repository.ApplicationRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    public static final List<ApplicationStatus> EXCLUDED_STATUSES = List.of(ApplicationStatus.DRAFT);
    private static final String AWS_S3_DIRECTORY = "application";

    private final ApplicationRepository applicationRepository;
    private final AwsS3Service awsS3Service;

    public long count() {
        return applicationRepository.count();
    }

    public Application findById(long id) {
        return applicationRepository.findById(id).get();
    }

    public Application findLatest() {
        return applicationRepository.findFirstByOrderByIdDesc().get();
    }

    public Page<Application> findAllByUserIdAndStatus(long userId, ApplicationStatus status, Pageable pageable) {
        if (status == null) {
            return applicationRepository.findAllByUserId(userId, pageable);
        }
        return applicationRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    public Page<Application> findAllByPostIdAndStatusForPostOwner(long postId, ApplicationStatus status, Pageable pageable) {
        if (status == null) {
            return applicationRepository.findAllByPostIdAndStatusNotIn(postId, EXCLUDED_STATUSES, pageable);
        }

        if (EXCLUDED_STATUSES.contains(status)) {
            throw new ServiceException("400-1", "허용되지 않는 상태값입니다.");
        }

        return applicationRepository.findAllByPostIdAndStatus(postId, status, pageable);
    }

    private Application create(Post post, User user, ApplicationStatus status, String content) {
        Application application = new Application(post, user, status, content);
        return applicationRepository.save(application);
    }

    private Application update(Application application, ApplicationStatus status, String content) {
        application.modify(status, content);
        return application;
    }

    public Application createOrUpdate(Post post, User actor, ApplicationWriteReqBody reqBody, List<MultipartFile> files) {
        ApplicationStatus status = ApplicationStatus.valueOf(reqBody.status());
        String content = reqBody.content();

        Optional<Application> existingApplication = applicationRepository.findByPostIdAndUserId(post.getId(), actor.getId());

        Application application;
        if (existingApplication.isEmpty()) {
            application = create(post, actor, status, content);
        } else {
            application = existingApplication.get();
            User user = application.getUser();
            application.checkActorCanModify(actor, user);
            application = update(application, status, content);

            application.getFiles().clear();
        }

        if (files != null && !files.isEmpty()) {
            List<String> fileUrls = awsS3Service.uploadFiles(files, AWS_S3_DIRECTORY);
            fileUrls.forEach(application::addApplicationFile);
        }

        return application;
    }

    public void updateStatus(Application application, ApplicationStatus status, boolean isApplicant) {
        application.modifyStatus(status, isApplicant);
    }

    public void delete(Application application) {
        List<String> fileKeys = application.getFiles().stream()
                .map(file -> awsS3Service.getFileKey(file.getUrl()))
                .toList();

        applicationRepository.delete(application);

        fileKeys.forEach(awsS3Service::deleteFile);
    }
}
