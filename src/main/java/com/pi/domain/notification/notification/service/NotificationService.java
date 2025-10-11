package com.pi.domain.notification.notification.service;

import com.pi.domain.notification.notification.dto.NotificationDto;
import com.pi.domain.notification.notification.entity.Notification;
import com.pi.domain.notification.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification findById(Long id) {
        return notificationRepository.findById(id).get();
    }


    @Transactional(readOnly = true)
    public List<NotificationDto> getItems(long userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(notification -> new NotificationDto((Notification) notification))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Notification notification = findById(id);

        notificationRepository.delete(notification);
    }


}
