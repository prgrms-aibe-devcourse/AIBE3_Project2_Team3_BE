package com.pi.domain.chat.chat.dto;

import com.pi.domain.user.user.dto.UserDto;

import java.util.List;

public record ChatRoomDto(
        Long id,
        String name,
        ChatMessageDto lastMessage,
        Long memberCount,
        String membershipStatus, // 내 관점: "ACTIVE"
        List<UserDto> avatarPreview, // size <= 3, 선택
        long unreadCount
) {
}
