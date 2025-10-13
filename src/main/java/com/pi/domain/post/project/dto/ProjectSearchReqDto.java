package com.pi.domain.post.project.dto;

import java.util.List;

public record ProjectSearchReqDto(
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds,
        Integer minSalary,
        Integer maxSalary,
        String keyword
) {
    public ProjectSearchReqDto {
        if (minSalary != null && maxSalary != null && minSalary > maxSalary) {
            throw new IllegalArgumentException("minSalary cannot be greater than maxSalary");
        }
    }


}
