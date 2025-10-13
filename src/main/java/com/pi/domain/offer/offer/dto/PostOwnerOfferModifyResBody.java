package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;

public record PostOwnerOfferModifyResBody(
        String status
) {
    public PostOwnerOfferModifyResBody(Offer offer) {
        this(
                offer.getStatus().name()
        );
    }
}
