package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;

public record OfferModifyStatusResBody(
        String status
) {
    public OfferModifyStatusResBody(Offer offer) {
        this(
                offer.getStatus().name()
        );
    }
}
