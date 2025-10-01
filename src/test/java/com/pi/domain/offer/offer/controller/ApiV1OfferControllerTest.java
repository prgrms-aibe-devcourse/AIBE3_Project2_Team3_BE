package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.service.OfferService;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1OfferControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private OfferService offerService;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Test
    @DisplayName("구인(삽니다) 등록하기")
    void t1() throws Exception {
        long freelancerId = 1;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/offers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "freelancerId": %d
                                        }""".formatted(freelancerId))
                )
                .andDo(print());

        Offer offer = offerService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(ApiV1OfferController.class))
                .andExpect(handler().methodName("create"))
//                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("%d번 구인이 등록되었습니다.".formatted(offer.getId())))
                .andExpect(jsonPath("$.data.id").value(offer.getId()))
                .andExpect(jsonPath("$.data.createdDate").value(Matchers.startsWith(offer.getCreatedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(offer.getModifiedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.freelancerId").value(offer.getFreelancer().getId()))
                .andExpect(jsonPath("$.data.userId").value(offer.getUser().getId()))
                .andExpect(jsonPath("$.data.status").value(OfferStatus.REQUESTED.name()));
    }
}
