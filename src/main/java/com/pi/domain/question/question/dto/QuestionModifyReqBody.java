package com.pi.domain.question.question.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionModifyReqBody(
        @NotBlank(message = "수정항 제목을 입력해주세요")
        String title,
        @NotBlank(message = "수정할 내용을 입력해주세요")
        String content
) {
}

