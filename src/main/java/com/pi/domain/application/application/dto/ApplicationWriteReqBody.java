package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ApplicationWriteReqBody(
        @NotNull Long postId,
        @NotNull @Min(50) String content,
        @NotNull @Min(0) Long salary,
        @NotNull @Min(0) Integer period
) {
}
