package com.pi.domain.chat.chat.dto;

import java.time.LocalDateTime;

public record ChatInviteDto(
        Long roomId,
        String roomName,
        LocalDateTime invitedDate
) {
}
