package com.pi.domain.category.category.dto;

public record CategoryCreateReqBody (
        String name,
        Long parentId
) {
}
