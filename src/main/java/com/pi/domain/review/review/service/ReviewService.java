package com.pi.domain.review.review.service;

import com.pi.domain.contract.contract.entity.Contract;
import com.pi.domain.contract.contract.repository.ContractRepository;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public Review create(User actor, Long contractId, int rating, String comment) {
        Contract contract = contractRepository.findById(contractId).get();

        if (reviewRepository.existsByContract(contract)) {
            throw new ServiceException("409-1", "이미 이 계약에 대한 리뷰가 존재합니다.");
        }

        Review review = new Review(contract, actor, rating, comment);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review modify(Long reviewId, User actor, int rating, String comment) {
        Review review = findById(reviewId);

        if (!review.isOwnedBy(actor)) {
            throw new ServiceException("403-1", "본인의 리뷰만 수정할 수 있습니다.");
        }

        review.modify(rating, comment);
        return review;
    }

    @Transactional
    public void delete(Long reviewId, User actor) {
        Review review = findById(reviewId);

        if (!review.isOwnedBy(actor)) {
            throw new ServiceException("403-2", "본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id).get();
    }
}
