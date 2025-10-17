package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

public record ApplicationModifyResBody(
        String content,
        long salary,
        long period
) {
    public ApplicationModifyResBody(Application application) {
        this(
                application.getContent(),
                application.getSalary(),
                application.getPeriod()
        );
    }
}
