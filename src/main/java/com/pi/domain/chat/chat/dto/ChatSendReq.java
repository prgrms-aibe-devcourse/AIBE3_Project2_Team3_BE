package com.pi.domain.chat.chat.dto;

public record ChatSendReq(
        Long roomId, String content
) {
}
