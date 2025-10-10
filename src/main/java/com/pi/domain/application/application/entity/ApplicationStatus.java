package com.pi.domain.application.application.entity;

import java.util.Map;
import java.util.Set;

public enum ApplicationStatus {
    DRAFT("임시저장"),
    APPLIED("제출"),
    ACCEPTED("수락"),
    REJECTED("거절");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static final Map<ApplicationStatus, Set<ApplicationStatus>> APPLICANT_TRANSITIONS = Map.of(
            DRAFT, Set.of(APPLIED)
    );

    public static final Map<ApplicationStatus, Set<ApplicationStatus>> POST_OWNER_TRANSITIONS = Map.of(
            APPLIED, Set.of(ACCEPTED, REJECTED),
            ACCEPTED, Set.of(REJECTED),
            REJECTED, Set.of(ACCEPTED)
    );

    public boolean canTransitionTo(ApplicationStatus next, boolean isApplicant) {
        Map<ApplicationStatus, Set<ApplicationStatus>> rules =
                isApplicant ? APPLICANT_TRANSITIONS : POST_OWNER_TRANSITIONS;
        return rules.getOrDefault(this, Set.of()).contains(next);
    }
}
