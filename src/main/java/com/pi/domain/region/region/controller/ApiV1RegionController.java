package com.pi.domain.region.region.controller;

import com.pi.domain.region.region.dto.RegionTreeDto;
import com.pi.domain.region.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/regions")
@Tag(name = "ApiV1RegionController", description = "API 지역 컨트롤러")
public class ApiV1RegionController {
    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "전체 카테고리 조회")
    public List<RegionTreeDto> getTreeAll() {
        return regionService.getTreeAll();
    }
}
