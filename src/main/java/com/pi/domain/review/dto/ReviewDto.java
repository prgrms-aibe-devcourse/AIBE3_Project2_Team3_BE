package com.pi.domain.review.dto;

import com.pi.domain.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewDto (
    long id,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate,
    long contractId,
    long userId,
    int rating,
    String comment
) {
    public ReviewDto(Review r) {
        this(
                r.getId(),
                r.getCreatedDate(),
                r.getModifiedDate(),
                r.getContract().getId(),
                r.getUser().getId(),
                r.getRating(),
                r.getComment()
        );
    }
}
