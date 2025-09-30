package com.pi.domain.freelancer.freelancerOffer.service;

import com.pi.domain.freelancer.freelancer.entity.Freelancer;
import com.pi.domain.freelancer.freelancerOffer.entity.FreelancerOffer;
import com.pi.domain.freelancer.freelancerOffer.entity.FreelancerOfferStatus;
import com.pi.domain.freelancer.freelancerOffer.repository.FreelancerOfferRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreelancerOfferService {
    private final FreelancerOfferRepository freelancerOfferRepository;

    public FreelancerOffer create(Freelancer freelancer, User user) {
        FreelancerOffer freelancerOffer = new FreelancerOffer(freelancer, user, FreelancerOfferStatus.REQUESTED);

        return freelancerOfferRepository.save(freelancerOffer);
    }
}
