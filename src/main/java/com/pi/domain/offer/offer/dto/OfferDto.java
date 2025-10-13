package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;

import java.time.LocalDateTime;

public record OfferDto(
        long id,
        int amount,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        long postId,
        long userId,
        String status
) {
    public OfferDto(Offer offer) {
        this(
                offer.getId(),
                offer.getAmount(),
                offer.getCreatedDate(),
                offer.getModifiedDate(),
                offer.getPost().getId(),
                offer.getUser().getId(),
                offer.getStatus().name()
        );
    }
}
