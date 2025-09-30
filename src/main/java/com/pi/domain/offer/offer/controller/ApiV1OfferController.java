package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/freelancers/{freelancer_id}/offers")
@RequiredArgsConstructor
public class ApiV1OfferController {
    private final OfferService offerService;

    @PostMapping
    @Transactional
    public void create() {

        Offer offer = offerService.create();
    }
}
