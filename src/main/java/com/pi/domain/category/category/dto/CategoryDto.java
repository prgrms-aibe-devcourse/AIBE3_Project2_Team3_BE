package com.pi.domain.category.category.dto;

import com.pi.domain.category.category.entity.Category;

import java.util.ArrayList;
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

    // querydsl에서 사용
    public CategoryDto(Long id, String name, Long parentId) {
        this(id, name, parentId, new ArrayList<>());
    }

    public static CategoryDto from(Category category, List<CategoryDto> children) {
        return new CategoryDto(category, children);
    }
}