package com.pi.domain.post.project.dto;

import java.util.List;

public record ProjectSearchReqDto(
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds,
        Long minSalary,
        Long maxSalary,
        String keyword
) {
    public ProjectSearchReqDto {
        if (minSalary != null && maxSalary != null && minSalary > maxSalary) {
            throw new IllegalArgumentException("최소 급여는 최대 급여보다 클 수 없습니다.");
        }
    }


}
