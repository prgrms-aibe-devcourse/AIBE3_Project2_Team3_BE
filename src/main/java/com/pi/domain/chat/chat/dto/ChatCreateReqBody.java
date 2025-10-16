package com.pi.domain.chat.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatCreateReqBody(
        @NotBlank String roomName,
        @NotNull List<Long> inviteeIds,
        Long offerId,
        Long applicationId
) {
}
