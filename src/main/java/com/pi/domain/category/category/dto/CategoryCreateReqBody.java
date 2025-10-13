package com.pi.domain.category.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateReqBody (
        @NotBlank @Schema(description = "카테고리명", example = "웹 개발")
        String name,
        @Schema(description = "부모 카테고리 ID (없으면 상위로 생성)", example = "10", nullable = true)
        Long parentId
) {
}
