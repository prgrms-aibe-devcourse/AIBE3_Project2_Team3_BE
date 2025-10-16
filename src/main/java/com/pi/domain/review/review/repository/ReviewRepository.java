package com.pi.domain.review.review.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.review.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByPostAndUserId(Post post, Long userId);

    Optional<Review> findByPost_IdAndUser_Id(Long postId, Long userId);

    Page<Review> findByPost_Id(Long postId, Pageable pageable);
}
