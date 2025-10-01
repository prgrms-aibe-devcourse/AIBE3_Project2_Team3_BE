package com.pi.domain.post.answer.dto;

public record AnswerCreateDto(
        String content,
        Long userId,
        Long questionId
) {}