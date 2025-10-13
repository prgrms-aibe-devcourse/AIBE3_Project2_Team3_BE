package com.pi.domain.review.review.dto;

import com.pi.domain.review.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        int rating,
        String userNickname,
        String targetNickname,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long postId
) {
    public ReviewDto(Review review) {
        this(
                review.getId(),
                review.getRating(),
                review.getUser().getNickname(),
                review.getPost().getUser().getNickname(),
                review.getComment(),
                review.getCreatedDate(),
                review.getModifiedDate(),
                review.getPost().getId()
        );
    }
}
