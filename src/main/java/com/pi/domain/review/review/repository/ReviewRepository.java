package com.pi.domain.review.review.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.review.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByPost(Post post);

    Page<Review> findByPost_Freelancer_Id(Long freelancerId, Pageable pageable);

    Page<Review> findByPost_Project_Id(Long projectId, Pageable pageable);
}
