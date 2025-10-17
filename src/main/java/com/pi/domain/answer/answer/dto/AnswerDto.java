package com.pi.domain.answer.answer.dto;

import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;

public record AnswerDto(
        Long id,
        String comment,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        UserDto user
) {
    public AnswerDto(Answer answer) {
        this(
                answer.getId(),
                answer.getComment(),
                answer.getCreatedDate(),
                answer.getModifiedDate(),
                new UserDto(answer.getUser())
        );
    }
}
