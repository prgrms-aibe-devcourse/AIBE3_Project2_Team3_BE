package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OfferModifyReqBody(
        @NotNull @Min(1) @Max(1000) Integer amount
) {
}
