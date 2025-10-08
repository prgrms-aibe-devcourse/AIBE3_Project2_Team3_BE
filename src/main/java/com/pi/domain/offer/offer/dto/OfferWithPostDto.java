package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.post.post.entity.Post;

import java.time.LocalDateTime;

public record OfferWithPostDto(
        long offerId,
        String offerStatus,
        LocalDateTime offerCreatedDate,
        long postId,
        long postUserId,
        String postUserNickname,
        String postTitle
) {
    public OfferWithPostDto(Offer offer, Post post) {
        this(
                offer.getId(),
                offer.getStatus().name(),
                offer.getCreatedDate(),
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle()
        );
    }
}
