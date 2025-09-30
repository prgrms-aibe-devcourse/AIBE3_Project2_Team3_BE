package com.pi.domain.freelancer.freelancerOffer.repository;

import com.pi.domain.freelancer.freelancerOffer.entity.FreelancerOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelancerOfferRepository extends JpaRepository<FreelancerOffer, Long> {
}
