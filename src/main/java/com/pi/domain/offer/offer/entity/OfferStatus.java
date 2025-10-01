package com.pi.domain.offer.offer.entity;

public enum OfferStatus {
    REQUESTED("요청"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    ;

    private final String description;

    OfferStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
