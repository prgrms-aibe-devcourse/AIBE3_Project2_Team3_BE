package com.pi.domain.offer.offer.repository;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findFirstByOrderByIdDesc();

    Optional<List<Offer>> findAllByUserId(long userId);

    Optional<List<Offer>> findAllByFreelancerId(Long freelancerId);

    Optional<List<Offer>> findAllByFreelancerIdAndStatus(Long freelancerId, OfferStatus status);
}
