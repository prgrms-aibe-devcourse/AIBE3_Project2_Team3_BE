package com.pi.domain.chat.chat.dto;

import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomDetailDto(
        Long roomId,
        String roomName,
        Long lastMessageId,                 // 없으면 null
        LocalDateTime LastMessageSendedDate,        // 없으면 방 생성시각
        long memberCount,
        String membershipStatus,            // "ACTIVE" | "PENDING" | "LEFT"
        List<UserDto> avatarPreview         // 최대 3명
) {
}
