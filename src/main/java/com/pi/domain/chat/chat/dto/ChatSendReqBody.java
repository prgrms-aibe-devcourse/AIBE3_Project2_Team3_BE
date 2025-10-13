package com.pi.domain.chat.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatSendReqBody(
        @NotBlank @Size(max = 4000) String content
) {
}
