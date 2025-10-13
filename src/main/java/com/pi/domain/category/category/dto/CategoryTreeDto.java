package com.pi.domain.category.category.dto;

import java.util.List;

public record CategoryTreeDto(
        Long id,
        String name,
        Long parentId,
        long childCount,
        List<CategoryTreeDto> children
) {
    public static CategoryTreeDto parent(Long id, String name) {
        return new CategoryTreeDto(id, name, null, 0L, List.of());
    }

    public static CategoryTreeDto child(Long id, String name, Long parentId) {
        return new CategoryTreeDto(id, name, parentId, 0L, List.of());
    }

    public CategoryTreeDto withChildren(List<CategoryTreeDto> kids) {
        List<CategoryTreeDto> copy = List.copyOf(kids); // 방어적 복사
        return new CategoryTreeDto(id, name, parentId, copy.size(), copy);
    }
}