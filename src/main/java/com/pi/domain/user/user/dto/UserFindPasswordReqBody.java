package com.pi.domain.user.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserFindPasswordReqBody(
        @NotBlank(message = "사용자 Id를 입력해주세요.")
        String username,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email
) {
}
