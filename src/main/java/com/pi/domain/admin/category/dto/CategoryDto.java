package com.pi.domain.admin.category.dto;

import com.pi.domain.admin.category.entity.Category;

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
}
