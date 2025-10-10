package com.pi.domain.admin.region.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/regions")
@Tag(name = "ApiV1AdminRegionController", description = "API 관리자 지역 컨트롤러")
public class RegionController {
}
