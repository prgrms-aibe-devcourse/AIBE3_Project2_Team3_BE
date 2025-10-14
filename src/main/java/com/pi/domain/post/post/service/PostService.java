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
    public Post increaseViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(""));
        post.increaseViewCount();
        return post;
    }
}
