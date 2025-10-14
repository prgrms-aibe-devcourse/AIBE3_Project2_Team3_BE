package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.dto.OfferWithUserDto;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/offers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1PostOfferController", description = "API 게시글 구인 컨트롤러")
public class ApiV1PostOfferController {
    private final Rq rq;
    private final OfferService offerService;
    private final FreelancerService freelancerService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public PagePayload<OfferWithUserDto> getItems(
            @PathVariable Long postId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) OfferStatus status
    ) {
        User actor = rq.getActor();
        Post post = freelancerService.findById(postId);
        post.checkActorCanReadOffer(actor);

        Page<OfferWithUserDto> dtoPage = offerService.findAllByPostIdAndStatus(postId, status, pageable)
                .map(offer -> new OfferWithUserDto(offer, offer.getUser()));

        return Ut.pageMapper.of(dtoPage);
    }
}
