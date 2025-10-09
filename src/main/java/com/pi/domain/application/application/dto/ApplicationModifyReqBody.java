package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationModifyReqBody(
        @NotNull ApplicationStatus status
) {
}
