package com.pi.domain.post.freelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FreelancerReqBody(
        @NotNull
        Long postId,

        @NotBlank
        String salary,

        @NotBlank
        String period
) {}
