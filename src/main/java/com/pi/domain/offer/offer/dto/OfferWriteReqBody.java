package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OfferWriteReqBody(
        @NotNull Long postId,
        @Min(1) @Max(100) Integer amount
) {
}