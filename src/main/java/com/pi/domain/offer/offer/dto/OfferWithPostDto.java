package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record OfferWithPostDto(
        long offerId,
        int amount,
        String offerStatus,
        LocalDateTime offerCreatedDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        long postUserId,
        String postUserNickname,
        String postTitle
) {
    public OfferWithPostDto(Offer offer, Post post, User user) {
        this(
                offer.getId(),
                offer.getAmount(),
                offer.getStatus().name(),
                offer.getCreatedDate(),
                post.getId(),
                "FREELANCER",
                user.getId(),
                user.getNickname(),
                post.getTitle()
        );
    }
}
