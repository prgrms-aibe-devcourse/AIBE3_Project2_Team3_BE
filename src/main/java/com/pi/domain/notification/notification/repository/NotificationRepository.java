package com.pi.domain.notification.notification.repository;

import com.pi.domain.notification.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Collection<Object> findByUserIdOrderByCreatedDateDesc(long id);

    Page<Notification> findByUserIdOrderByCreatedDateDesc(long id, Pageable pageable);
}
