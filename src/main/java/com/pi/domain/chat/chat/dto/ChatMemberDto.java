package com.pi.domain.chat.chat.dto;

import java.time.LocalDateTime;

public record ChatMemberDto (
        Long userId,
        String nickname,
        String profileImageUrl,
        String role,               // OWNER / MEMBER
        String membershipStatus,   // ACTIVE / PENDING
        LocalDateTime startedDate,  // ACTIVE면 입장시각, PENDING이면 null,
        LocalDateTime endedDate
){
}
