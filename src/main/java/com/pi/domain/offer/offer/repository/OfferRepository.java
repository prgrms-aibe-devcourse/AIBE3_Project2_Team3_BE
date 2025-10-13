package com.pi.domain.offer.offer.repository;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findFirstByOrderByIdDesc();

    Page<Offer> findAllByUserIdAndStatus(long userId, OfferStatus status, Pageable page);

    Page<Offer> findAllByPostIdAndStatus(long postId, OfferStatus status, Pageable page);

    Page<Offer> findAllByPostId(Long postId, Pageable pageable);
}
