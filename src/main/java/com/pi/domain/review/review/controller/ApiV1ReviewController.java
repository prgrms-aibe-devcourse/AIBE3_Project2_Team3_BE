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

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ApiV1ReviewController {

    private final ReviewService reviewService;
    private final Rq rq;

    @PostMapping("/{contractId}")
    public RsData<ReviewDto> create(
            @PathVariable Long contractId,
            @RequestBody ReviewReqBody reqBody
    ) {
        User actor = rq.getActor();
        Review review = reviewService.create(actor, contractId, reqBody.rating(), reqBody.comment());
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
}
