package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ApplicationWriteReqBody(
        Long id,

        @NotNull
        Long postId,

        @NotNull
        @Pattern(regexp = "DRAFT|APPLIED")
        ApplicationStatus status,

        String content
) {
}
