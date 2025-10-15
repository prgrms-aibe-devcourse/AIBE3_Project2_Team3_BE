package com.pi.domain.application.application.entity;

import java.util.Map;
import java.util.Set;

public enum ApplicationStatus {
    PENDING("대기"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    COMPLETED("프로젝트 완료");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static final Map<ApplicationStatus, Set<ApplicationStatus>> POST_OWNER_TRANSITIONS = Map.of(
            PENDING, Set.of(PENDING, ACCEPTED, REJECTED),
            ACCEPTED, Set.of(REJECTED, COMPLETED),
            REJECTED, Set.of(ACCEPTED),
            COMPLETED, Set.of()
    );

    public boolean canTransitionTo(ApplicationStatus next) {
        Map<ApplicationStatus, Set<ApplicationStatus>> rules = POST_OWNER_TRANSITIONS;
        return rules.getOrDefault(this, Set.of()).contains(next);
    }
}
