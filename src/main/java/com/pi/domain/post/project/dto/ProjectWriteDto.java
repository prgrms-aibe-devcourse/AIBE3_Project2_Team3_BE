package com.pi.domain.post.project.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ProjectWriteDto(
        @NotBlank
        LocalDateTime deadlineDate,
        @NotBlank
        LocalDateTime startedDate,
        @NotBlank
        LocalDateTime endedDate,
        @NotBlank
        String hirerType,
        @NotBlank
        String employmentType,
        @NotBlank
        Long salary,
        @NotBlank
        Integer personnel,
        @NotBlank
        Integer skillLevel
) {
}
