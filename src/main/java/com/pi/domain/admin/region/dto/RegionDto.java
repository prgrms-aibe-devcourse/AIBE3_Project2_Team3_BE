package com.pi.domain.admin.region.dto;

import com.pi.domain.admin.region.entity.Region;

public record RegionDto(
        Long id,
        String name
) {
    public RegionDto(Region region) {
        this(
                region.getId(),
                region.getName()
        );
    }
}
