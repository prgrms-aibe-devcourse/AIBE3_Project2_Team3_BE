package com.pi.domain.answer.answer.dto;

import com.pi.domain.answer.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerDto(
        Long id,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        Long userId,
        Long questionId
) {
    public AnswerDto(Answer answer) {
        this(
                answer.getId(),
                answer.getContent(),
                answer.getCreatedDate(),
                answer.getModifiedDate(),
                answer.getUser().getId(),
                answer.getQuestion().getId()
        );
    }
}
