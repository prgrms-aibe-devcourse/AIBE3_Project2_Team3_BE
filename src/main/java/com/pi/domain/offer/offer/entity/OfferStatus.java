package com.pi.domain.offer.offer.entity;

import java.util.Map;
import java.util.Set;

public enum OfferStatus {
    PENDING("대기"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    COMPLETED("구매 확정");

    private final String description;

    OfferStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static final Map<OfferStatus, Set<OfferStatus>> OFFER_OWNER_TRANSITIONS = Map.of(
            PENDING, Set.of(),
            ACCEPTED, Set.of(COMPLETED),
            REJECTED, Set.of(),
            COMPLETED, Set.of()
    );

    public static final Map<OfferStatus, Set<OfferStatus>> POST_OWNER_TRANSITIONS = Map.of(
            PENDING, Set.of(ACCEPTED, REJECTED),
            ACCEPTED, Set.of(REJECTED),
            REJECTED, Set.of(ACCEPTED),
            COMPLETED, Set.of()
    );

    public boolean canTransitionTo(OfferStatus next, boolean isOfferOwner) {
        Map<OfferStatus, Set<OfferStatus>> rules = isOfferOwner ? OFFER_OWNER_TRANSITIONS : POST_OWNER_TRANSITIONS;
        return rules.getOrDefault(this, Set.of()).contains(next);
    }
}
