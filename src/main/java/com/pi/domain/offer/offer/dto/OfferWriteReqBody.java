package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OfferWriteReqBody(
        @NotNull Long postId,
        @NotNull @Min(1) @Max(1000) Integer amount
) {
}