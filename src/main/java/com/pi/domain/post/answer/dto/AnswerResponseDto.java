package com.pi.domain.post.answer.dto;

import com.pi.domain.post.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        String author
) {
    public static AnswerResponseDto from(Answer answer) {
        return new AnswerResponseDto(
                answer.getId(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getAuthor()
        );
    }
}