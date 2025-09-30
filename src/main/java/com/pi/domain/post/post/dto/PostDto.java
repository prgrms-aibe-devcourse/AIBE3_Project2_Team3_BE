package com.pi.domain.post.post.dto;

public record PostDto(
        String title,
        String content,
        Boolean isViewed
) {
}