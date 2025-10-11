package com.pi.domain.notification.notification.repository;

import com.pi.domain.post.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<Project, Long> {
    Collection<Object> findByUserIdOrderByCreatedDateDesc(long id);
}
