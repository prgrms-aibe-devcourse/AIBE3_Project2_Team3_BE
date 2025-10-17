package com.pi.domain.application.application.service;

import com.pi.domain.application.application.dto.ApplicationModifyReqBody;
import com.pi.domain.application.application.dto.ApplicationWriteReqBody;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.repository.ApplicationRepository;
import com.pi.domain.application.file.entity.ApplicationFile;
import com.pi.domain.chat.chat.repository.ChatRoomRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private static final String AWS_S3_DIRECTORY = "application";
    private final ApplicationRepository applicationRepository;
    private final AwsS3Service awsS3Service;
    private final ChatRoomRepository chatRoomRepository;

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

    public Page<Application> findAllByPostUserIdAndStatus(long postUserId, ApplicationStatus status, Pageable pageable) {
        if (status == null) {
            return applicationRepository.findAllByPostUserId(postUserId, pageable);
        }
        return applicationRepository.findAllByPostUserIdAndStatus(postUserId, status, pageable);
    }

    public Page<Application> findAllByPostIdAndStatus(long postId, ApplicationStatus status, Pageable pageable) {
        if (status == null) return applicationRepository.findAllByPostId(postId, pageable);
        return applicationRepository.findAllByPostIdAndStatus(postId, status, pageable);
    }

    public Application create(Post post, User actor, ApplicationWriteReqBody reqBody, List<MultipartFile> files) {
        if (applicationRepository.existsByPostAndUser(post, actor)) {
            log.warn("중복 지원 불가. 게시글: {}, 사용자: {}", post.getId(), actor.getId());
            throw new ServiceException("409-1", "이미 존재하는 데이터입니다.");
        }

        Application application = new Application(post, actor, reqBody.content(), reqBody.salary(), reqBody.period());
        applicationRepository.save(application);

        createFiles(application, files);

        return application;
    }

    public void update(Application application, ApplicationModifyReqBody reqBody, List<MultipartFile> files, List<Long> removeFileIds) {
        application = update(application, reqBody.content(), reqBody.salary(), reqBody.period());

        deleteFiles(application, removeFileIds);
        createFiles(application, files);
    }

    public void updateStatus(Application application, String status) {
        application.modifyStatus(ApplicationStatus.valueOf(status));
    }

    public void delete(Application application) {
        chatRoomRepository.findByOfferId(application.getId()).ifPresent(room -> {
            room.setApplication(null);
        });

        List<String> fileKeys = application.getFiles().stream()
                .map(file -> awsS3Service.getDecodedFileKey(file.getUrl()))
                .toList();

        applicationRepository.delete(application);

        fileKeys.forEach(awsS3Service::deleteFile);
    }

    private Application update(Application application, String content, Long salary, Long period) {
        application.modify(content, salary, period);
        return application;
    }

    private void createFiles(Application application, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        List<String> fileUrls = awsS3Service.uploadFiles(files, AWS_S3_DIRECTORY);
        fileUrls.forEach(application::addApplicationFile);
    }

    private void deleteFiles(Application application, List<Long> removeIds) {
        if (removeIds != null && !removeIds.isEmpty()) {
            List<ApplicationFile> toRemove = application.getFiles().stream()
                    .filter(f -> removeIds.contains(f.getId()))
                    .toList();

            for (ApplicationFile file : toRemove) {
                String fileKey = awsS3Service.getDecodedFileKey(file.getUrl());
                awsS3Service.deleteFile(fileKey);
                application.getFiles().remove(file);
            }
        }
    }
}
