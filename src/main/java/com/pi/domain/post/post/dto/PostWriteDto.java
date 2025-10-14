package com.pi.domain.post.post.dto;

import jakarta.validation.constraints.NotBlank;

public record PostWriteDto(
        @NotBlank
        String title,
        @NotBlank
        String content,
        boolean isViewed
) {

}
