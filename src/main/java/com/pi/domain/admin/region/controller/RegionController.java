package com.pi.domain.admin.region.controller;

import com.pi.domain.admin.region.dto.RegionCreateReqBody;
import com.pi.domain.admin.region.dto.RegionDto;
import com.pi.domain.admin.region.entity.Region;
import com.pi.domain.admin.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/regions")
@Tag(name = "ApiV1AdminRegionController", description = "API 관리자 지역 컨트롤러")
public class RegionController {
    private final RegionService regionService;

    @Operation(summary = "지역 생성")
    @PostMapping
    public RegionDto createRegion(@RequestBody RegionCreateReqBody dto) {
        Region region = regionService.createRegion(dto);
        return RegionDto.from(region, List.of());
    }

    @GetMapping
    @Operation(summary = "전체 지역 트리 조회")
    public List<RegionDto> getRegionTree() {
        return regionService.getRegionTree();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지역 삭제")
    public void deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
    }
}
