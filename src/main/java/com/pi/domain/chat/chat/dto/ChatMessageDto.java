package com.pi.domain.chat.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long SenderId,
        String senderNickname,
        String senderProfileImageUrl,
        String content,
        LocalDateTime createdDate
) {
}
