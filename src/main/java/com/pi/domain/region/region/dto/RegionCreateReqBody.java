package com.pi.domain.region.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegionCreateReqBody (
        @NotBlank @Schema(description = "지역명", example = "서울")
        String name,
        @Schema(description = "부모 카테고리 ID (없으면 상위로 생성)", example = "10", nullable = true)
        Long parentId
) {}
