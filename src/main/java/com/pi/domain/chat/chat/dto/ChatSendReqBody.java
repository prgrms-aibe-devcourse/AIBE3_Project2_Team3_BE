package com.pi.domain.chat.chat.dto;

public record ChatSendReqBody(
        Long roomId, String content
) {
}
