package com.pi.domain.post.post.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.reaction.reaction.repository.ReactionRepository;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public Post increaseViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException());
        post.increaseViewCount();
        return post;
    }
//
//    public ProjectDto findByIdWithLikeStatus(Long postId, Long currentUserId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new RuntimeException());
//
//        boolean isLiked = false;
//        if (currentUserId != null) {
//            User user = userRepository.findById(currentUserId)
//                    .orElseThrow(() -> new RuntimeException());
//            isLiked = reactionRepository.existsByPostAndUser(post, user);
//        }
//
//        return new ProjectDto(post, isLiked);
//    }
}
