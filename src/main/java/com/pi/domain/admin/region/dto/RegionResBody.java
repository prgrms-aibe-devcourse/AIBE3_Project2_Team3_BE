package com.pi.domain.admin.region.dto;

import com.pi.domain.admin.region.entity.Region;

import java.util.List;

public record RegionResBody (
        Long id,
        String name,
        Long parentId,
        List<RegionResBody> children
        ) {
    public static RegionResBody from(Region region, List<RegionResBody> children) {
        return new RegionResBody(
                region.getId(),
                region.getName(),
                region.getParent() != null ? region.getParent().getId() : null,
                children
        );
    }
}
