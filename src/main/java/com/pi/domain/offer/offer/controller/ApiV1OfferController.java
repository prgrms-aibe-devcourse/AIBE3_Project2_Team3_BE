package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.dto.OfferDto;
import com.pi.domain.offer.offer.dto.OfferModifyReqBody;
import com.pi.domain.offer.offer.dto.OfferWithPostDto;
import com.pi.domain.offer.offer.dto.OfferWriteReqBody;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1OfferController", description = "API 구인 컨트롤러")
public class ApiV1OfferController {
    private final Rq rq;
    private final OfferService offerService;
    private final FreelancerService freelancerService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public PagePayload<OfferWithPostDto> getMyItems(
            @ParameterObject @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) OfferStatus status
    ) {
        User actor = rq.getActor();
        Page<OfferWithPostDto> dtoPage = offerService.findAllByUserIdAndStatus(actor.getId(), status, pageable)
                .map(offer -> new OfferWithPostDto(offer, offer.getPost(), offer.getUser()));

        return Ut.pageMapper.of(dtoPage);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public OfferDto getMyItem(@PathVariable Long id) {
        User actor = rq.getActor();
        Offer offer = offerService.findById(id);
        User user = offer.getUser();
        offer.checkActorCanRead(actor, user);

        return new OfferDto(offer);
    }

    @PostMapping
    @Transactional
    @Operation(summary = "등록")
    public RsData<OfferDto> write(@Valid @RequestBody OfferWriteReqBody reqBody) {
        User actor = rq.getActor();
        Post post = freelancerService.findById(reqBody.postId());
        post.checkActorIsNotOwner(actor);

        Offer offer = offerService.create(post, actor, reqBody.amount());

        return new RsData<>(
                "201-1",
                "%d번 구인이 등록되었습니다.".formatted(offer.getId()),
                new OfferDto(offer)
        );
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<OfferDto> modify(
            @PathVariable long id,
            @Valid @RequestBody OfferModifyReqBody reqBody
    ) {
        Offer offer = offerService.findById(id);

        User actor = rq.getActor();
        offer.checkActorCanModify(actor);

        if (offer.getStatus() != OfferStatus.PENDING) {
            log.warn("수락/거절된 구인({})은 수정 불가", offer.getId());
            throw new ServiceException("400-1", "잘못된 요청입니다.");
        }
        offerService.update(offer, reqBody.amount());

        return new RsData<>(
                "200-1",
                "%d번 구인이 수정되었습니다.".formatted(id),
                new OfferDto(offer)
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public RsData<Void> delete(@PathVariable Long id) {
        Offer offer = offerService.findById(id);

        User actor = rq.getActor();
        offer.checkActorCanDelete(actor);

        offerService.delete(offer);

        return new RsData<>("200-1", "%d번 구인이 삭제되었습니다.".formatted(id));
    }
}
