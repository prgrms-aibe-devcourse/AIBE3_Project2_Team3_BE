package com.pi.domain.post.question.dto;

import com.pi.domain.post.answer.dto.AnswerResponseDto;
import com.pi.domain.post.question.entity.Question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record QuestionResponseDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        List<AnswerResponseDto> answers
) {
    public QuestionResponseDto(Question question) {
        this(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedAt(),
                question.getModifiedAt(),
                question.getAnswers().stream()
                        .map(AnswerResponseDto::from)
                        .collect(Collectors.toList())
        );
    }
}