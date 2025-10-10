package com.pi.domain.post.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    ONGOING("모집중"),
    CLOSED("마감됨");

    private final String displayValue;

    ProjectStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator
    public static ProjectStatus fromDisplayValue(String value) {
        for (ProjectStatus status : values()) {
            if (status.displayValue.equalsIgnoreCase(value.trim())
                    || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStatus value: " + value);
    }
}