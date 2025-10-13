package com.pi.domain.review.review.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByPost(Post post);

    List<Review> findByPost_Freelancer_Id(Long freelancerId);

    List<Review> findByPost_Project_Id(Long projectId);
}
