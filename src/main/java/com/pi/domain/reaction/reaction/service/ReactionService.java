package com.pi.domain.reaction.reaction.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.reaction.reaction.entity.Reaction;
import com.pi.domain.reaction.reaction.repository.ReactionRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (reactionRepository.existsByPostAndUser(post, user)) {
            reactionRepository.deleteByPostAndUser(post, user);
            post.decreaseLikeCount();
        } else {
            Reaction reaction = new Reaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reactionRepository.save(reaction);
            post.increaseLikeCount();
        }
    }
}
