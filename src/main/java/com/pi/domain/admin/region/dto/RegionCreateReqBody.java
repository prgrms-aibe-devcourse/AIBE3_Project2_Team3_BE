package com.pi.domain.admin.region.dto;

public record RegionCreateReqBody (
    String name,
    Long parentId
) {}
