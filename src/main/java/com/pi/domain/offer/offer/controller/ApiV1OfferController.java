package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.dto.*;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(name = "ApiV1OfferController", description = "API 구인(삽니다) 컨트롤러")
public class ApiV1OfferController {
    private final Rq rq;
    private final OfferService offerService;
    private final FreelancerService freelancerService;

    @GetMapping("/my")
    @Transactional(readOnly = true)
    @Operation(summary = "본인이 등록한 구인 조회")
    public RsData<PagedResBody<OfferWithPostDto>> getMyOffers(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) OfferStatus status
    ) {
        User actor = rq.getActor();
        Page<Offer> pagedOffers = offerService.findAllByUserIdAndStatus(actor.getId(), status, pageable);

        return new RsData<>(
                "200-1",
                "%d번 사용자의 구인이 조회되었습니다.".formatted(actor.getId()),
                new PagedResBody<>(
                        pagedOffers.getContent().stream()
                                .map(offer -> new OfferWithPostDto(offer, offer.getFreelancer().getPost()))
                                .toList()
                        , pagedOffers
                )
        );
    }

    @GetMapping("/freelancer/{freelancerId}")
    @Transactional(readOnly = true)
    @Operation(summary = "프리랜서의 구인 조회")
    public RsData<PagedResBody<OfferWithUserDto>> getOffersForFreelancer(
            @PathVariable Long freelancerId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) OfferStatus status
    ) {
        User actor = rq.getActor();
        Freelancer freelancer = freelancerService.findById(freelancerId);
        freelancer.checkActorCanReadOffer(actor);

        Page<Offer> pagedOffers = offerService.findAllByFreelancerIdAndStatus(freelancerId, status, pageable);

        return new RsData<>(
                "200-1",
                "%d번 프리랜서의 구인이 조회되었습니다.".formatted(freelancer.getId()),
                new PagedResBody<>(
                        pagedOffers.getContent().stream()
                                .map(OfferWithUserDto::new)
                                .toList()
                        , pagedOffers
                )
        );
    }

    @PostMapping
    @Transactional
    @Operation(summary = "등록")
    public RsData<OfferDto> write(@Valid @RequestBody OfferWriteReqBody reqBody) {
        User actor = rq.getActor();
        Freelancer freelancer = freelancerService.findById(reqBody.freelancerId());

        Offer offer = offerService.create(freelancer, actor);

        return new RsData<>(
                "201-1",
                "%d번 구인이 등록되었습니다.".formatted(offer.getId()),
                new OfferDto(offer)
        );
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<Void> modify(
            @PathVariable long id,
            @Valid @RequestBody OfferModifyReqBody reqBody
    ) {
        Offer offer = offerService.findById(id);

        User actor = rq.getActor();
        User freelancerUser = offerService.getFreelancerUser(offer);
        offer.checkActorCanModify(actor, freelancerUser);

        offerService.update(offer, reqBody.status());

        return new RsData<>(
                "200-1",
                "%d번 구인 상태가 수정되었습니다.".formatted(id)
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public RsData<OfferDto> delete(@PathVariable Long id) {
        Offer offer = offerService.findById(id);

        User actor = rq.getActor();
        offer.checkActorCanDelete(actor);

        offerService.delete(offer);

        return new RsData<>("200-1", "%d번 구인이 삭제되었습니다.".formatted(id), new OfferDto(offer));
    }
}
