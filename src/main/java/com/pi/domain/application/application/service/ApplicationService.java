package com.pi.domain.application.application.service;

import com.pi.domain.application.application.dto.ApplicationWriteReqBody;
import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.application.application.repository.ApplicationRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public long count() {
        return applicationRepository.count();
    }

    public Optional<Application> findById(long id) {
        return applicationRepository.findById(id);
    }

    public Application findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new ServiceException("404-1", "%d번 구직을 찾을 수 없습니다.".formatted(id)));
    }

    public Application findLatest() {
        return applicationRepository.findFirstByOrderByIdDesc().get();
    }

    public Page<Application> findAllByUserIdAndStatus(long userId, ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    public Page<Application> findAllByPostIdAndStatus(long postId, ApplicationStatus status, Pageable pageable) {
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

    public Application createOrUpdate(Post post, User actor, ApplicationWriteReqBody reqBody) {
        if (reqBody.id() == null) {
            if (applicationRepository.existsByPostIdAndUserId(post.getId(), actor.getId())) {
                throw new ServiceException("409-1", "이미 해당 공고에 지원했습니다.");
            }

            return create(post, actor, reqBody.status(), reqBody.content());
        }

        Application existingApplication = findByIdOrThrow(reqBody.id());

        User user = existingApplication.getUser();
        existingApplication.checkActorCanModify(actor, user);

        return update(existingApplication, reqBody.status(), reqBody.content());
    }

    public void updateStatus(Application application, ApplicationStatus status, boolean isApplicant) {
        application.modifyStatus(status, isApplicant);
    }

    public void delete(Application application) {
        applicationRepository.delete(application);
    }
}
