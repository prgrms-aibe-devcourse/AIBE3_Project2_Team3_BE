package com.pi.domain.post.answer.dto;

import com.pi.domain.post.answer.entity.Answer;
import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;

public record AnswerResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserDto author
) {
    public static AnswerResponseDto from(Answer answer) {
        return new AnswerResponseDto(
                answer.getId(),
                answer.getContent(),
                answer.getCreatedAt(),
                new UserDto(answer.getUser())
        );
    }
}