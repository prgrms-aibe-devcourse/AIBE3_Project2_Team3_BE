package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.ApplicationStatus;

public record ApplicationModifyResBody(
        ApplicationStatus status
) {
}
