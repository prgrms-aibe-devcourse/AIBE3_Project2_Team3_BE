package com.pi.domain.category.category.dto;

import com.pi.domain.category.category.entity.Category;

public record CategoryDto(
        Long id,
        String name
) {
    public CategoryDto(Category category) {
        this(
                category.getId(),
                category.getName()
        );
    }

    // 이름만 반환
    public CategoryDto(String name) {
        this(
                null,
                name
        );
    }
}
