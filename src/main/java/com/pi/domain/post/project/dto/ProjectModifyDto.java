package com.pi.domain.post.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ProjectModifyDto(
        @NotNull
        LocalDateTime deadlineDate,
        @NotNull
        LocalDateTime startedDate,
        @NotNull
        LocalDateTime endedDate,
        @NotBlank
        String hirerType,
        @NotBlank
        String employmentType,
        @NotNull @Positive
        Long salary,
        @NotNull @Positive
        Integer personnel,
        @NotNull @Positive
        Integer skillLevel
) {
}
