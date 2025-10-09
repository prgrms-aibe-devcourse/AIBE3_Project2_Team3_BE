package com.pi.domain.question.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionModifyReqBody(
        @NotBlank(message = "수정할 제목을 입력해주세요")
        @Size(max = 50, message = "제목은 50자 이하로 입력해주세요")
        String title,
        @NotBlank(message = "수정할 내용을 입력해주세요")
        @Size(max = 200, message = "질문은 200자 이하로 작성해주세요")
        String content
) {
}

