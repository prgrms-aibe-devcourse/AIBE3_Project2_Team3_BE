package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.dto.OfferCreateReqBody;
import com.pi.domain.offer.offer.dto.OfferDto;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.service.FreelancerService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class ApiV1OfferController {
    private final Rq rq;
    private final OfferService offerService;
    private final FreelancerService freelancerService;

    @PostMapping
    @Transactional
    public RsData<OfferDto> create(
            @PathVariable long freelancerId,
            @Valid @RequestBody OfferCreateReqBody reqBody
    ) {
        User actor = rq.getActor();
        Freelancer freelancer = freelancerService.findById(freelancerId);

        Offer offer = offerService.create(freelancer, actor);

        return new RsData<>(
                "201-1",
                "%d번 구인이 등록되었습니다.".formatted(offer.getId()),
                new OfferDto(offer)
        );
    }
}
