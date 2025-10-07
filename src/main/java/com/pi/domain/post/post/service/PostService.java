package com.pi.domain.post.post.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post findById(Long id) {
        return postRepository.findById(id).get();
    }

    public Page<Post> getPage(Pageable pageable, String searchKeyword) {
        return postRepository.findAllByTitle(pageable, searchKeyword);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
}
