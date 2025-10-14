package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.Min;

public record ApplicationModifyReqBody(
        String content,
        @Min(0) Long salary,
        @Min(0) Integer period
) {
}
