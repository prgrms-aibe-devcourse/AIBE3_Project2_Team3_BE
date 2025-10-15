package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OfferModifyStatusReqBody(
        @NotNull
        @Pattern(regexp = "PENDING|ACCEPTED|REJECTED")
        String status
) {
}
