package com.pi.domain.offer.offer.service;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.repository.OfferRepository;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final FreelancerService freelancerService;

    public long count() { return offerRepository.count();}

    public Offer findById(long id) {
        return offerRepository.findById(id).get();
    }

    public Offer findLatest() {
        return offerRepository.findFirstByOrderByIdDesc().get();
    }

    public Offer create(Freelancer freelancer, User user) {
        Offer offer = new Offer(freelancer, user, OfferStatus.REQUESTED);

        return offerRepository.save(offer);
    }

    public void update(Offer offer, OfferStatus status) {
        offer.setStatus(status);
    }

    public void delete(Offer offer) {
        offerRepository.delete(offer);
    }

    public User getFreelancerUser(Offer offer) {
        Freelancer freelancer = freelancerService.findById(offer.getFreelancer().getId());
        return freelancer.getPost().getUser();
    }

    public List<Offer> getOffersByUserId(long userId) {
        return offerRepository.findAllByUserId(userId).get();
    }

    public List<Offer> getOffersByFreelancerId(Long freelancerId) {
        return offerRepository.findAllByFreelancerId(freelancerId).get();
    }
}
