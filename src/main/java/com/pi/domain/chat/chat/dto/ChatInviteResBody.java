package com.pi.domain.chat.chat.dto;

import java.util.List;

public record ChatInviteResBody (
        List<Long> invited,
        List<Skip> skipped
){
    public record Skip(Long id, String reason) {}
}
