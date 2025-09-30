package com.pi.domain.freelancer.freelancerOffer.entity;

public enum FreelancerOfferStatus {
    REQUESTED("요청"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    ;

    private final String description;

    FreelancerOfferStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
