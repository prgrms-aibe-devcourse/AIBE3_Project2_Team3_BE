package com.pi.domain.review.review.dto;

import com.pi.domain.review.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        String writerNickname,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
    public ReviewDto(Review review) {
        this(
                review.getId(),
                review.getWriter().getNickname(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
