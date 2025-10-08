package com.pi.domain.answer.answer.dto;

public record AnswerCreateReqBody(
        String content,
        Long questionId
) {
}