package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;

import java.time.LocalDateTime;

public record OfferDto(
        long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        long freelancerId,
        long userId,
        String status
) {
    public OfferDto(Offer offer) {
        this(
                offer.getId(),
                offer.getCreatedDate(),
                offer.getModifiedDate(),
                offer.getFreelancer().getId(),
                offer.getUser().getId(),
                offer.getStatus().name()
        );
    }
}
