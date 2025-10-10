package com.pi.domain.admin.region.dto;

import com.pi.domain.admin.region.entity.Region;

import java.util.List;

public record RegionDto(
        Long id,
        String name,
        Long parentId,
        List<RegionDto> children
) {
    public RegionDto(Region region) {
        this(
                region.getId(),
                region.getName(),
                region.getParent() != null ? region.getParent().getId() : null,
                null
        );
    }

    public RegionDto(Region region, List<RegionDto> children) {
        this(
                region.getId(),
                region.getName(),
                region.getParent() != null ? region.getParent().getId() : null,
                children
        );
    }

    public static RegionDto from(Region region, List<RegionDto> children) {
        return new RegionDto(region, children);
    }
}
