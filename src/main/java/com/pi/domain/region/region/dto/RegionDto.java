package com.pi.domain.region.region.dto;

import com.pi.domain.region.region.entity.Region;

public record RegionDto(
        Long id,
        String name,
        Long parentId
) {
    public RegionDto(Region region) {
        this(
                region.getId(),
                region.getName(),
                region.getParent() != null ? region.getParent().getId() : null
        );
    }
}
