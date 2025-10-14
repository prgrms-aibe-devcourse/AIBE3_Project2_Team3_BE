package com.pi.domain.user.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserInviteSearchReqBody (
        @NotBlank String username
) {
}
