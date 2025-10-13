package com.pi.domain.offer.offer.service;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.repository.OfferRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public long count() {
        return offerRepository.count();
    }

    public Offer findById(long id) {
        return offerRepository.findById(id).get();
    }

    public Offer findLatest() {
        return offerRepository.findFirstByOrderByIdDesc().get();
    }

    public Page<Offer> findAllByUserIdAndStatus(long id, OfferStatus status, Pageable pageable) {
        return offerRepository.findAllByUserIdAndStatus(id, status, pageable);
    }

    public Page<Offer> findAllByPostIdAndStatus(Long postId, OfferStatus status, Pageable pageable) {
        return offerRepository.findAllByPostIdAndStatus(postId, status, pageable);
    }

    public Offer create(Post post, User user, int amount) {
        Offer offer = new Offer(post, user, OfferStatus.REQUESTED, amount);

        return offerRepository.save(offer);
    }

    public void update(Offer offer, OfferStatus status) {
        offer.setStatus(status);
    }

    public void delete(Offer offer) {
        offerRepository.delete(offer);
    }
}
