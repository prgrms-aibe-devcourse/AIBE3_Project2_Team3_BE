package com.pi.domain.post.freelancer.dto;

import jakarta.validation.constraints.NotBlank;

public record FreelancerReqBody(
   //    Long postId,

        @NotBlank
        String title,

        @NotBlank
        String content,

        @NotBlank
        boolean isViewed,

        @NotBlank
        String salary,

        @NotBlank
        String period
) {}
