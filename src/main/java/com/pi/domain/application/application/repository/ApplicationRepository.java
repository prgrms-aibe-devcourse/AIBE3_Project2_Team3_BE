package com.pi.domain.application.application.repository;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.application.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findFirstByOrderByIdDesc();

    Page<Application> findAllByUserIdAndStatus(long userId, ApplicationStatus status, Pageable page);

    Page<Application> findAllByPostIdAndStatus(long postId, ApplicationStatus status, Pageable page);

    boolean existsByPostIdAndUserId(long postId, long userId);
}
