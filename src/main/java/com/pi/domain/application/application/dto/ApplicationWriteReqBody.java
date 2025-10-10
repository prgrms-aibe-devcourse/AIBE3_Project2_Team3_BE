package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ApplicationWriteReqBody(
        Long id,

        @NotNull
        Long postId,

        @NotNull
        @Pattern(regexp = "DRAFT|APPLIED", message = "상태 값은 DRAFT 또는 APPLIED 이어야 합니다.")
        String status,

        String content
) {
}
