package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record OfferWithUserDto(
        long id,
        String status,
        int amount,
        LocalDateTime createdDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        String postTitle,
        long userId,
        String userNickname
) {
    public OfferWithUserDto(Offer offer, Post post, User user) {
        this(
                offer.getId(),
                offer.getStatus().name(),
                offer.getAmount(),
                offer.getCreatedDate(),
                post.getId(),
                "FREELANCER",
                post.getTitle(),
                user.getId(),
                user.getNickname()
        );
    }
}