package com.pi.domain.user.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserModifyReqBody(
        @NotBlank
        @Size(min = 2, max = 20)
        String nickname,
        @NotBlank
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email
) {
}
