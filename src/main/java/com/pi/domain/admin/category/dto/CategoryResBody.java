package com.pi.domain.admin.category.dto;

import com.pi.domain.admin.category.entity.Category;

import java.util.List;

public record CategoryResBody(
        Long id,
        String name,
        Long parentId,
        List<CategoryResBody> children
) {
    public static CategoryResBody from(Category category, List<CategoryResBody> children) {
        return new CategoryResBody(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                children
        );
    }
}