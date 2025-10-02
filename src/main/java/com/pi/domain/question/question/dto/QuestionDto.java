package com.pi.domain.question.question.dto;

import com.pi.domain.answer.answer.dto.AnswerDto;
import com.pi.domain.question.question.entity.Question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record QuestionDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        List<AnswerDto> answers
) {
    public QuestionDto(Question question) {
        this(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedDate(),
                question.getModifiedDate(),
                question.getAnswers().stream()
                        .map(AnswerDto::new)
                        .collect(Collectors.toList())
        );
    }
}