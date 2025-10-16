package com.pi.domain.post.post.repository;

import com.pi.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByFreelancerIsNotNullAndId(Long id);

    Page<Post> findByFreelancerIsNotNullAndUser_Id(Long userId, Pageable pageable);

    Optional<Post> findByProjectIsNotNullAndId(Long id);

    Optional<Post> findTopByOrderByIdDesc();

    Page<Post> findByUser_IdAndProjectIsNotNull(Long id, Pageable pageable);
    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :postId")
    int increaseView(@Param("postId") Long postId);

    @Query("select p.viewCount from Post p where p.id = :postId")
    long getViewCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.likeCount = p.likeCount + 1 where p.id = :postId")
    int increaseLike(@Param("postId") Long postId);

    @Modifying
    @Query("""
           update Post p set p.likeCount = 
             case when p.likeCount > 0 then p.likeCount - 1 else 0 end
           where p.id = :postId
           """)
    int decreaseLike(@Param("postId") Long postId);

    @Query("select p.likeCount from Post p where p.id = :postId")
    long getLikeCount(@Param("postId") Long postId);
}
