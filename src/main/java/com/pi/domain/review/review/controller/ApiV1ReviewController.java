package com.pi.domain.review.review.controller;

import com.pi.domain.review.review.dto.ReviewDto;
import com.pi.domain.review.review.dto.ReviewModifyReqBody;
import com.pi.domain.review.review.dto.ReviewWriteReqBody;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.service.ReviewService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "ApiV1ReviewController", description = "리뷰 API 컨트롤러")
public class ApiV1ReviewController {

    private final ReviewService reviewService;
    private final Rq rq;

    @GetMapping("/my")
    @Operation(summary = "내가 작성한 리뷰 목록")
    public RsData<PagePayload<ReviewDto>> getMyReviews(Pageable pageable) {
        User actor = rq.getActor();
        Page<ReviewDto> dtoPage = reviewService.findByUser(actor, pageable).map(ReviewDto::new);
        return new RsData<>("200-1", "내 리뷰 목록입니다.", Ut.pageMapper.of(dtoPage));
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "특정 계약의 리뷰 목록")
    public RsData<PagePayload<ReviewDto>> getReviewsByContract(
            @PathVariable Long contractId,
            Pageable pageable
    ) {
        Page<ReviewDto> dtoPage = reviewService.findByContract(contractId, pageable).map(ReviewDto::new);
        return new RsData<>("200-1", "계약별 리뷰 목록입니다.", Ut.pageMapper.of(dtoPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "리뷰 단건 조회")
    public RsData<ReviewDto> getReview(@PathVariable Long id) {
        Review review = reviewService.findById(id);
        return new RsData<>("200-1", "%d번 리뷰 조회".formatted(id), new ReviewDto(review));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "리뷰 작성")
    public RsData<ReviewDto> write(@Valid @RequestBody ReviewWriteReqBody reqBody) {
        User actor = rq.getActor();
        RsData<Review> rs = reviewService.create(actor, reqBody.contractId(), reqBody.rating(), reqBody.comment());
        return new RsData<>(rs.resultCode(), rs.message(), new ReviewDto(rs.data()));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "리뷰 수정")
    public RsData<ReviewDto> modify(
            @PathVariable Long id,
            @Valid @RequestBody ReviewModifyReqBody reqBody
    ) {
        User actor = rq.getActor();
        Review review = reviewService.findById(id);

        RsData<Review> rs = reviewService.modify(review, actor, reqBody.rating(), reqBody.comment());
        return new RsData<>(rs.resultCode(), rs.message(), new ReviewDto(rs.data()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "리뷰 삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        User actor = rq.getActor();
        Review review = reviewService.findById(id);

        return reviewService.delete(review, actor);
    }
}
