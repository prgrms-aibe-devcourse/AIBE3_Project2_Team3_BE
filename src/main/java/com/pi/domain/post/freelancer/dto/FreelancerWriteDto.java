package com.pi.domain.post.freelancer.dto;

import jakarta.validation.constraints.NotBlank;

public record FreelancerWriteDto(
        @NotBlank
        Long salary,
        @NotBlank
        Long period
) {
}
