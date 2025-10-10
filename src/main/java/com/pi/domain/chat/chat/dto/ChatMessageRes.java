package com.pi.domain.chat.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageRes(
        Long MessageId, Long roomId, Long senderUserId,
        String content, LocalDateTime createdAt
) {
}
