package com.pi.domain.post.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

//TODO: 프로젝트단 상태관리 기능 필요시 사용
public enum ProjectStatus {
    ONGOING("모집중"),
    CLOSED("마감됨");

    private final String displayValue;

    ProjectStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    // 문자열 반환 직렬화 "모집중"이렇게 내보냄
    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    // 문자열 받아 역직렬화
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