package com.pi.domain.region.region.controller;

import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.dto.RegionTreeDto;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/regions")
@Tag(name = "ApiV1AdminRegionController", description = "API 관리자 지역 컨트롤러")
public class ApiV1AdmRegionController {
    private final RegionService regionService;

    @Operation(summary = "지역 생성")
    @PostMapping
    public RegionTreeDto create(@RequestBody RegionCreateReqBody reqBody) {
        Region r = regionService.create(reqBody);
        return RegionTreeDto.child(r.getId(), r.getName(), r.getParent() != null ? r.getParent().getId() : null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지역 삭제")
    public void delete(@PathVariable Long id) {
        regionService.delete(id);
    }
}
