package com.pi.domain.offer.offer.dto;

import jakarta.validation.constraints.NotNull;

public record OfferCreateReqBody(
        @NotNull Long freelancerId
) {
}