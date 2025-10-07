package com.pi.domain.post.post.dto;

import com.pi.domain.post.post.entity.Post;

public record PostDto(
        String title,
        String content,
        Boolean isViewed
) {
    public PostDto(Post post) {
        this(
                post.getTitle(),
                post.getContent(),
                post.isViewed()
        );
    }
}