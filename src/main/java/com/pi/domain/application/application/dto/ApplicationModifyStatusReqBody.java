package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ApplicationModifyStatusReqBody(
        @NotNull
        @Pattern(regexp = "PENDING|ACCEPTED|REJECTED|COMPLETED")
        String status
) {
}
