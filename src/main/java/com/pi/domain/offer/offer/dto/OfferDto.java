package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record OfferDto(
        @NonNull long id,
        @NonNull LocalDateTime createdDate,
        @NonNull LocalDateTime modifiedDate,
        @NonNull long freelancerId,
        @NonNull long userId,
        @NonNull String status
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
