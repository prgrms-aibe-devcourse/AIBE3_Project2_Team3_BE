package com.pi.domain.category.category.dto;

import com.pi.domain.category.category.entity.Category;

import java.util.List;

public record CategoryDto(
        Long id,
        String name,
        Long parentId,
        List<CategoryDto> children
) {
    public CategoryDto(Category category) {
        this(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                null
        );
    }

    public CategoryDto(Category category, List<CategoryDto> children) {
        this(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                children
        );
    }

    public static CategoryDto from(Category category, List<CategoryDto> children) {
        return new CategoryDto(category, children);
    }
}