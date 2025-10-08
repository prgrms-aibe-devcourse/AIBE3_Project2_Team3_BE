package com.pi.domain.answer.answer.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerModifyReqBody(
        @NotBlank
        String content
) { }

