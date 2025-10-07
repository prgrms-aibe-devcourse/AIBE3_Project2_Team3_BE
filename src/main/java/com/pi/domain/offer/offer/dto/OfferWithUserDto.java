package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;

import java.time.LocalDateTime;

public record OfferWithUserDto(
        long id,
        String status,
        LocalDateTime createdDate,
        long userId,
        String userNickname
) {
    public OfferWithUserDto(Offer offer) {
        this(
                offer.getId(),
                offer.getStatus().name(),
                offer.getCreatedDate(),
                offer.getUser().getId(),
                offer.getUser().getNickname()
        );
    }
}
