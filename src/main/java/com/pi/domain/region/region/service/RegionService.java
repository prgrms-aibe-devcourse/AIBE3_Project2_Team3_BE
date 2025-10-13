package com.pi.domain.region.region.service;

import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Page<RegionDto> getRegions(Pageable pageable) {
        return regionRepository.findAll(pageable)
                .map(region -> RegionDto.from(region, List.of()));
    }


    @Transactional
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
