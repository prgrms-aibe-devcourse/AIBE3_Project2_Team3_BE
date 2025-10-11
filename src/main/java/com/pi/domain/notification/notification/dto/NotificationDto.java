package com.pi.domain.notification.notification.dto;

import com.pi.domain.notification.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String content

) {

    public NotificationDto(Notification notification) {
        this(
                notification.getId(),
                notification.getCreatedDate(),
                notification.getModifiedDate(),
                notification.getContent()
        );
    }
}
