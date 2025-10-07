package com.pi.domain.post.freelancer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FreelancerModifyDto(
        @NotNull @Positive
        Long salary,
        @NotNull @Positive
        Long period
) {
}
