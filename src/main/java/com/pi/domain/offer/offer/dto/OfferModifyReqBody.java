package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record OfferModifyReqBody(
        @Min(1) @Max(100) Integer amount
) {
}
