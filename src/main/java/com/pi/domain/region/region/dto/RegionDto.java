package com.pi.domain.region.region.dto;

import com.pi.domain.region.region.entity.Region;

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
