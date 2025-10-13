package com.pi.domain.category.category.dto;

import com.pi.domain.category.category.entity.Category;

public record CategoryDto(
        Long id,
        String name,
        Long parentId
) {
    public CategoryDto(Category category) {
        this(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
        );
    }
}