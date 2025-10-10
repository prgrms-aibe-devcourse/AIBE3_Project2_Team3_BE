package com.pi.domain.review.service;

import com.pi.domain.contract.entity.Contract;
import com.pi.domain.contract.repository.ContractRepository;
import com.pi.domain.review.entity.Review;
import com.pi.domain.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContractRepository contractRepository;

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "리뷰를 찾을 수 없습니다."));
    }

    public Page<Review> findByUser(User user, Pageable pageable) {
        return reviewRepository.findByUserId(user.getId(), pageable);
    }

    public Page<Review> findByContract(Long contractId, Pageable pageable) {
        return reviewRepository.findByContractId(contractId, pageable);
    }

    @Transactional
    public RsData<Review> create(User actor, Long contractId, Integer rating, String comment) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ServiceException("404-2", "해당 계약이 존재하지 않습니다."));

        if (reviewRepository.existsByUserIdAndContractId(actor.getId(), contractId)) {
            throw new ServiceException("400-1", "이미 이 계약에 리뷰를 작성했습니다.");
        }

        Review review = new Review(contract, actor, rating, comment);
        reviewRepository.save(review);

        return new RsData<>("200-1", "리뷰가 등록되었습니다.", review);
    }

    @Transactional
    public RsData<Review> modify(Review review, User actor, Integer rating, String comment) {
        review.checkActorCanModify(actor);
        review.modify(rating, comment);

        return new RsData<>("200-2", "리뷰가 수정되었습니다.", review);
    }

    @Transactional
    public RsData<Void> delete(Review review, User actor) {
        review.checkActorCanDelete(actor);
        reviewRepository.delete(review);

        return new RsData<>("200-3", "리뷰가 삭제되었습니다.");
    }
}
