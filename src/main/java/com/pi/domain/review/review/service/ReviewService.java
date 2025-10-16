package com.pi.domain.review.review.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.review.review.dto.ReviewDto;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;

    @Transactional
    public Review create(User actor, Long postId, int rating, String comment) {
        Post post = postRepository.findById(postId).get();

        if (reviewRepository.existsByPostAndUserId(post, actor.getId())) {
            throw new ServiceException("409-1", "이미 이 게시글에 대한 리뷰가 존재합니다.");
        }

        Review review = new Review(post, actor, rating, comment);
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

    public Page<ReviewDto> findReviewsByPost(Long postId, Pageable pageable) {
        return reviewRepository.findByPost_Id(postId, pageable)
                .map(ReviewDto::new);
    }

    public long count() {
        return reviewRepository.count();
    }

    public Optional<ReviewDto> findMyReviewByPost(Long postId, Long userId) {
        return reviewRepository.findByPost_IdAndUser_Id(postId, userId)
                .map(ReviewDto::new);
    }

}
