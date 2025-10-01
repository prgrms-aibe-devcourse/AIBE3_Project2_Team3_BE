package com.pi.domain.user.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserJoinReqBody(
        @NotBlank
        @Size(min = 4, max = 20)
        String username,
        @NotBlank
        @Size(min = 4, max = 30)
        String password,
        @NotBlank
        @Size(min = 2, max = 20)
        String nickname
) {
}
