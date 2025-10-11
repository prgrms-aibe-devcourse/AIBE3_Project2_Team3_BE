package com.pi.domain.notification.notification.service;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.entity.Notification;
import com.pi.domain.notification.notification.repository.NotificationRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationDto read(User user, Long id) {

        return null;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getItems(User actor) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(actor.getId())
                .stream()
                .map(notification -> new NotificationDto(
                        (Notification) notification
                ))
                .toList();
    }


    public void readAll() {
    }

    public void delete(Long id) {
    }

    public Long countUnread() {
    }

    public void create(NotificationDto notificationDto) {
    }


}
