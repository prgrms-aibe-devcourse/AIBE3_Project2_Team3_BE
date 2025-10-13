package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ApplicationWriteReqBody(
        @NotNull Long postId,
        String content,
        @Min(0) Long salary,
        @Min(0) Integer period
) {
}
