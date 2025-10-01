package com.pi.domain.post.question.dto;

public record QuestionCreateDto (
        String title,
        String content,
        Long userId
) {}
