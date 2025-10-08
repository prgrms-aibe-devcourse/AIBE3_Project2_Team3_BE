package com.pi.domain.answer.answer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerModifyReqBody(
        @NotBlank(message = "수정할 내용을 입력해주세요")
        @Size(max = 500, message = "답변은 500자 이하로 작성해주세요")
        String content
) { }

