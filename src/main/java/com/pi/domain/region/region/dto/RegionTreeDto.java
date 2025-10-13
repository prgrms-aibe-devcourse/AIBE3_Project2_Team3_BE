package com.pi.domain.region.region.dto;

import java.util.List;

public record RegionTreeDto (
        Long id,
        String name,
        Long parentId,
        long childCount,
        List<RegionTreeDto> children
) {
    public static RegionTreeDto parent(Long id, String name) {
        return new RegionTreeDto(id, name, null, 0L, List.of());
    }

    public static RegionTreeDto child(Long id, String name, Long parentId) {
        return new RegionTreeDto(id, name, parentId, 0L, List.of());
    }

    public RegionTreeDto withChildren(List<RegionTreeDto> kids) {
        List<RegionTreeDto> copy = List.copyOf(kids); // 방어적 복사
        return new RegionTreeDto(id, name, parentId, copy.size(), copy);
    }
}