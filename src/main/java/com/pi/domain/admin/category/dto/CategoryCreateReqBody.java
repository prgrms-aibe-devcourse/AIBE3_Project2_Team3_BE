package com.pi.domain.admin.category.dto;

public record CategoryCreateReqBody (
        String name,
        Long parentId
) {
}
