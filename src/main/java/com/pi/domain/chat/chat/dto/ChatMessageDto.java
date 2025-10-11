package com.pi.domain.chat.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long senderId,
        String senderNickname,
        String senderProfileImageUrl,
        String content,
        LocalDateTime createdDate
) {
}
