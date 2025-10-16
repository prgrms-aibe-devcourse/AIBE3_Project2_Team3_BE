package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record PostOfferWithUserDto(
        long id,
        String status,
        int amount,
        LocalDateTime createdDate,
        long userId,
        String userNickname
) {
    public PostOfferWithUserDto(Offer offer, User user) {
        this(
                offer.getId(),
                offer.getStatus().name(),
                offer.getAmount(),
                offer.getCreatedDate(),
                user.getId(),
                user.getNickname()
        );
    }
}
