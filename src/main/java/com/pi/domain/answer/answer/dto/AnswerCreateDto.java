package com.pi.domain.answer.answer.dto;

public record AnswerCreateDto(
        String content,
        Long userId,
        Long questionId
) {}