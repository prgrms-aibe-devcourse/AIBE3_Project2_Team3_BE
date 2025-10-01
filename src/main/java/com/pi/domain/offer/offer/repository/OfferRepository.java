package com.pi.domain.offer.offer.repository;

import com.pi.domain.offer.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findFirstByOrderByIdDesc();
}
