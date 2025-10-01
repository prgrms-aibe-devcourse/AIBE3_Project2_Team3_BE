package com.pi.domain.post.freelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FreelancerRequestDto(

        @NotNull(message = "게시글 ID는 필수입니다.")
        Long postId,

        @NotBlank(message = "급여 정보는 비워둘 수 없습니다.")
        String salary,

        @NotBlank(message = "작업 기간은 비워둘 수 없습니다.")
        String period
) {}
