package com.pi.domain.application.application.dto;

import jakarta.validation.constraints.NotNull;

public record PostOwnerApplicationModifyReqBody(
        @NotNull String status
) {
}
