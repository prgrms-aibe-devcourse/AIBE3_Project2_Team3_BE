package com.pi.domain.review.repository;

import com.pi.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(long id, Pageable pageable);
    Page<Review> findByContractId(Long contractId, Pageable pageable);
    boolean existsByUserIdAndContractId(long id, Long contractId);
}
