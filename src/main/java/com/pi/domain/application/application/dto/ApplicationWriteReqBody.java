package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationWriteReqBody(
        @NotNull Long postId,
        @NotNull @Size(min = 50) String content,
        @NotNull @Min(0) Long salary,
        @NotNull @Min(1) Long period
) {
}
