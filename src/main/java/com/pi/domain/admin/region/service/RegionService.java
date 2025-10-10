package com.pi.domain.admin.region.service;

import com.pi.domain.admin.region.dto.RegionCreateReqBody;
import com.pi.domain.admin.region.dto.RegionDto;
import com.pi.domain.admin.region.entity.Region;
import com.pi.domain.admin.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    @Transactional
    public Region createRegion(RegionCreateReqBody dto) {
        boolean exists;
        Region parent = null;

        if (dto.parentId() != null) {
            parent = regionRepository.findById(dto.parentId())
                    .get();
            exists = regionRepository.findByParentId(dto.parentId())
                    .stream().anyMatch(r -> r.getName().equals(dto.name()));
        } else {
            exists = regionRepository.findByParentIsNull()
                    .stream().anyMatch(r -> r.getName().equals(dto.name()));
        }
        if (exists) throw new IllegalArgumentException("동일 이름 지역 존재");

        Region region = new Region();
        region.setName(dto.name());
        if (parent != null) parent.addChild(region);

        return regionRepository.save(region);
    }

    @Transactional(readOnly = true)
    public List<RegionDto> getRegionTree() {
        List<Region> roots = regionRepository.findByParentIsNull();
        return roots.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }

    private RegionDto buildTree(Region region) {
        List<RegionDto> children = regionRepository.findByParentId(region.getId()).stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        return new RegionDto(
                region.getId(),
                region.getName(),
                region.getParent() != null ? region.getParent().getId() : null,
                children
        );
    }

    @Transactional
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
