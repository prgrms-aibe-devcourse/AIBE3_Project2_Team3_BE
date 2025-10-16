package com.pi.domain.reaction.reaction.repository;

import com.pi.domain.reaction.reaction.entity.Reaction;
import com.pi.domain.reaction.reaction.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    boolean existsByPost_IdAndUser_IdAndType(Long postId, Long userId, ReactionType type);
    long deleteByPost_IdAndUser_IdAndType(Long postId, Long userId, ReactionType type);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query("""
    delete from Reaction r
    where r.post.id = :postId
      and r.user.id = :userId
      and r.type    = :type
  """)
    int deleteLike(@Param("postId") Long postId,
                   @Param("userId") Long userId,
                   @Param("type") ReactionType type);
}
