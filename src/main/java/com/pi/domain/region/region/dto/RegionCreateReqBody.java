package com.pi.domain.region.region.dto;

public record RegionCreateReqBody (
    String name,
    Long parentId
) {}
