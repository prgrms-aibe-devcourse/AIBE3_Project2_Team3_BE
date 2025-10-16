package com.pi.domain.reaction.reaction.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.reaction.reaction.entity.Reaction;
import com.pi.domain.reaction.reaction.entity.ReactionType;
import com.pi.domain.reaction.reaction.repository.ReactionRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, Long userId) {
        return reactionRepository.existsByPost_IdAndUser_IdAndType(postId, userId, ReactionType.LIKE);
        // 기존 existsByPostIdAndUserId 는 타입 구분이 안 돼서 LIKE/BOOKMARK 혼선 위험
    }

    @Transactional
    public boolean toggleLike(User actor, Long postId) {
        Long userId = actor.getId();

        if (reactionRepository.existsByPost_IdAndUser_IdAndType(postId, userId, ReactionType.LIKE)) {
            int deleted = reactionRepository.deleteLike(postId, userId, ReactionType.LIKE);
            if (deleted > 0) postRepository.decreaseLike(postId);
            return false; // OFF
        }

        Reaction r = new Reaction();
        r.setPost(postRepository.getReferenceById(postId)); // ref 권장
        User u = new User(); u.setId(userId); r.setUser(u);
        r.setType(ReactionType.LIKE);

        try {
            reactionRepository.save(r);
            postRepository.increaseLike(postId);
            return true; // ON
        } catch (DataIntegrityViolationException e) {
            return true; // 경합 멱등
        }
    }

    @Transactional
    public boolean likeOn(User actor, Long postId) {
        Long userId = actor.getId();
        if (reactionRepository.existsByPost_IdAndUser_IdAndType(postId, userId, ReactionType.LIKE)) {
            return true; // 이미 ON → 멱등
        }
        Reaction r = new Reaction();
        r.setPost(new Post()); r.getPost().setId(postId);
        r.setUser(new User()); r.getUser().setId(userId);
        r.setType(ReactionType.LIKE);

        try {
            reactionRepository.save(r);
            postRepository.increaseLike(postId);
            return true;
        } catch (DataIntegrityViolationException e) {
            return true; // 경합 시 멱등
        }
    }

    @Transactional
    public boolean likeOff(User actor, Long postId) {
        Long userId = actor.getId();
        long deleted = reactionRepository.deleteByPost_IdAndUser_IdAndType(postId, userId, ReactionType.LIKE);
        if (deleted > 0) {
            postRepository.decreaseLike(postId);
            return true; // off 성공
        }
        return false; // 이미 off였음(멱등)
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        return postRepository.getLikeCount(postId);
    }
}
