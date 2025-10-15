package com.pi.domain.offer.offer.dto;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record OfferWithPostDto(
        long id,
        String status,
        LocalDateTime createdDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        String postTitle,
        long postUserId,
        String postUserNickname
) {
    public OfferWithPostDto(Offer offer, Post post, User postUser) {
        this(
                offer.getId(),
                offer.getStatus().name(),
                offer.getCreatedDate(),
                post.getId(),
                "FREELANCER",
                post.getTitle(),
                postUser.getId(),
                postUser.getNickname()
        );
    }
}
