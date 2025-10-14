package com.pi.domain.reaction.reaction.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.reaction.reaction.entity.Reaction;
import com.pi.domain.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

@Controller
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    boolean existsByPostAndUser(Post post, User user);

    void deleteByPostAndUser(Post post, User user);

    long countByPost(Post post);
}
