package com.pi.domain.admin.category.dto;

import com.pi.domain.admin.category.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

public record CategoryResBody(
        Long id,
        String name,
        Long parentId,
        List<CategoryResBody> children
) {
    public static CategoryResBody from(Category category) {
        return new CategoryResBody(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getChildren() != null
                        ? category.getChildren().stream().map(CategoryResBody::from).collect(Collectors.toList())
                        : List.of()
        );
    }
}