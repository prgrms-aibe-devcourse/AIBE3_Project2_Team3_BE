package com.pi.domain.offer.offer.service;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.repository.OfferRepository;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public Offer create(Freelancer freelancer, User user) {
        Offer offer = new Offer(freelancer, user, OfferStatus.REQUESTED);

        return offerRepository.save(offer);
    }

    public Optional<Offer> findLatest() {
        return offerRepository.findFirstByOrderByIdDesc();
    }
}
