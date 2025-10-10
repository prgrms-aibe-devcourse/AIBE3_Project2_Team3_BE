package com.pi.domain.post.project.entity;

public enum ProjectStatus {
    ONGOING("모집중"),
    CLOSED("마감됨");

    private final String displayValue;

    ProjectStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public static ProjectStatus fromDisplayValue(String displayValue) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.displayValue.equalsIgnoreCase(displayValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStatus value: " + displayValue);
    }
}