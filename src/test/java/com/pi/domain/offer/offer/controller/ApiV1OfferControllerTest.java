package com.pi.domain.offer.offer.controller;

import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.offer.offer.entity.OfferStatus;
import com.pi.domain.offer.offer.service.OfferService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1OfferControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private OfferService offerService;

    @Test
    @DisplayName("구인(삽니다) 등록")
    @WithUserDetails("user1")
    void t1() throws Exception {
        long freelancerId = 1;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/offers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "freelancerId": %d
                                        }
                                        """.formatted(freelancerId))
                )
                .andDo(print());

        Offer offer = offerService.findLatest();

        resultActions
                .andExpect(handler().handlerType(ApiV1OfferController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("%d번 구인이 등록되었습니다.".formatted(offer.getId())))
                .andExpect(jsonPath("$.data.id").value(offer.getId()))
                .andExpect(jsonPath("$.data.createdDate").value(Matchers.startsWith(offer.getCreatedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(offer.getModifiedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.freelancerId").value(offer.getPost().getId()))
                .andExpect(jsonPath("$.data.userId").value(offer.getUser().getId()))
                .andExpect(jsonPath("$.data.status").value(OfferStatus.PENDING.name()));
    }

    @Test
    @DisplayName("구인 상태 수정")
    @WithUserDetails("user1")
    void t2() throws Exception {
        long offerId = 1;
        Offer offer = offerService.findById(offerId);

        String status = OfferStatus.ACCEPTED.name();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/offers/" + offerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "%s"
                                        }""".formatted(status))
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1OfferController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%d번 구인 상태가 수정되었습니다.".formatted(offerId)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("구인 삭제")
    @WithUserDetails("user2")
    void t3() throws Exception {
        long offerId = 1;
        Offer offer = offerService.findById(offerId);

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/offers/" + offerId)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1OfferController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("%d번 구인이 삭제되었습니다.".formatted(offerId)));
    }
}
