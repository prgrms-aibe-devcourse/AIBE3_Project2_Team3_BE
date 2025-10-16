package com.pi.domain.post.post.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public long increaseViewCount(Long postId) {
        int updated = postRepository.increaseView(postId);
        if (updated == 0) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }
        return postRepository.getViewCount(postId);
    }
}
