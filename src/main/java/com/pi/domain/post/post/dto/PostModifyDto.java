package com.pi.domain.post.post.dto;

import jakarta.validation.constraints.NotBlank;

public record PostModifyDto(
        @NotBlank
        String title,
        @NotBlank
        String content,
        boolean isViewed
) {

}
