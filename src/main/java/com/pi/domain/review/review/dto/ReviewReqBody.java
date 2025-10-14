package com.pi.domain.review.review.dto;

public record ReviewReqBody(
        int rating,
        String comment
) {}
