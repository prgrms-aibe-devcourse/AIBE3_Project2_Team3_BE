package com.pi.domain.notification.notification.dto;

import com.pi.domain.notification.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String type,
        Long relatedEntityId,
        String title,
        String content

) {

    public NotificationDto(Notification notification) {
        this(
                notification.getId(),
                notification.getCreatedDate(),
                notification.getModifiedDate(),
                determineType(notification),
                extractRelatedEntityId(notification),
                generateTitle(notification),
                notification.getContent()
        );
    }


    private static String determineType(Notification n) {
        if (n.getOffer() != null) return "OFFER";
        if (n.getChatMessage() != null) return "CHAT";
        if (n.getReview() != null) return "REVIEW";
        if (n.getContract() != null) return "CONTRACT";
        return "GENERAL";
    }

    // 페이지 이동을 위한 관련 엔티티
    private static Long extractRelatedEntityId(Notification n) {
        if (n.getOffer() != null) return n.getOffer().getId();
        if (n.getChatMessage() != null) return n.getChatMessage().getId();
        if (n.getReview() != null) return n.getReview().getId();
        if (n.getContract() != null) return n.getContract().getId();

        return null;
    }

    //엔티티에 title 필드가 없어서 dto에서 생성
    private static String generateTitle(Notification n) {
        return switch (determineType(n)) {
            case "OFFER" -> "새로운 제안이 도착했어요";
            case "CHAT" -> "새 메시지가 도착했습니다";
            case "APPLY" -> "지원 상태가 변경되었습니다";
            case "CONTRACT" -> "계약 관련 알림이 있습니다";
            case "REVIEW" -> "새 리뷰가 등록되었습니다";
            default -> "알림";
        };
    }

}
