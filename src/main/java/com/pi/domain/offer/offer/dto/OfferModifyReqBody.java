package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.OfferStatus;
import jakarta.validation.constraints.NotNull;

public record OfferModifyReqBody(
        @NotNull OfferStatus status,
        int amount
) {
}
