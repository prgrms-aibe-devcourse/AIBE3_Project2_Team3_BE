package com.pi.domain.application.application.repository;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findFirstByOrderByIdDesc();

    Page<Application> findAllByUserId(long userId, Pageable pageable);

    Page<Application> findAllByUserIdAndStatus(long userId, ApplicationStatus status, Pageable page);

    Page<Application> findAllByPostIdAndStatus(long postId, ApplicationStatus status, Pageable page);

    Optional<Application> findByPostIdAndUserId(long postId, long userId);

    Page<Application> findAllByPostId(long postId, Pageable pageable);

    Page<Application> findAllByPostUserId(Long postUserId, Pageable pageable);

    Page<Application> findAllByPostUserIdAndStatus(Long postUserId, ApplicationStatus status, Pageable pageable);

    boolean existsByPostAndUser(Post post, User actor);
}
