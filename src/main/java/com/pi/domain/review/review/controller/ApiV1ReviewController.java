package com.pi.domain.review.review.controller;

import com.pi.domain.review.review.dto.ReviewDto;
import com.pi.domain.review.review.dto.ReviewReqBody;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.service.ReviewService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ApiV1ReviewController {

    private final ReviewService reviewService;
    private final Rq rq;

    @PostMapping("/{postId}")
    public RsData<ReviewDto> create(
            @PathVariable Long postId,
            @RequestBody ReviewReqBody reqBody
    ) {
        User actor = rq.getActor();
        Review review = reviewService.create(actor, postId, reqBody.rating(), reqBody.comment());
        return new RsData<>("200-1", "리뷰가 작성되었습니다.", new ReviewDto(review));
    }

    @PutMapping("/{reviewId}")
    public RsData<ReviewDto> modify(
            @PathVariable Long reviewId,
            @RequestBody ReviewReqBody reqBody
    ) {
        User actor = rq.getActor();
        Review review = reviewService.modify(reviewId, actor, reqBody.rating(), reqBody.comment());
        return new RsData<>("200-2", "리뷰가 수정되었습니다.", new ReviewDto(review));
    }

    @DeleteMapping("/{reviewId}")
    public RsData<Void> delete(@PathVariable Long reviewId) {
        User actor = rq.getActor();
        reviewService.delete(reviewId, actor);
        return new RsData<>("200-3", "리뷰가 삭제되었습니다.");
    }

    @GetMapping("/{reviewId}")
    public RsData<ReviewDto> getOne(@PathVariable Long reviewId) {
        Review review = reviewService.findById(reviewId);
        return new RsData<>("200-4", "리뷰 조회 성공", new ReviewDto(review));
    }

    @GetMapping("/freelancer/{freelancerId}/reviews")
    public RsData<List<ReviewDto>> getFreelancerReviews(@PathVariable Long freelancerId) {
        List<ReviewDto> reviews = reviewService.findReviewsByFreelancer(freelancerId);
        return new RsData<>("200-5", "프리랜서 리뷰 조회 성공", reviews);
    }

    @GetMapping("/project/{projectId}/reviews")
    public RsData<List<ReviewDto>> getProjectReviews(@PathVariable Long projectId) {
        List<ReviewDto> reviews = reviewService.findReviewsByProject(projectId);
        return new RsData<>("200-6", "프로젝트 리뷰 조회 성공", reviews);
    }
}
