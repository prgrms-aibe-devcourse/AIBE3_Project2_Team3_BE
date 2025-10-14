package com.pi.domain.region.region.service;

import com.pi.domain.region.region.dto.RegionCreateReqBody;
import com.pi.domain.region.region.dto.RegionTreeDto;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionQueryRepository;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final RegionQueryRepository regionQueryRepository;

    public long count() {
        return regionRepository.count();
    }

    @Transactional
    public Region create(RegionCreateReqBody reqBody) {
        Region parent = null;
        if (reqBody.parentId() != null) {
            parent = regionRepository.findById(reqBody.parentId()).orElseThrow(
                    () -> new ServiceException("400-1", "존재하지 않는 부모 카테고리입니다.")
            );
            // ✅ 손자 금지: 부모의 parent가 있으면 거부
            if (parent.getParent() != null) {
                throw new ServiceException("400-2", "하위 레벨은 1까지만 가능합니다.");
            }
        }

        return regionRepository.save(new Region(reqBody.name().trim(), parent));
    }

    @Transactional(readOnly = true)
    public List<RegionTreeDto> getTreeAll() {
        List<Region> parents = regionQueryRepository.findParentsWithChildrenAll(); // fetch join

        return parents.stream().map(p -> {
            // 부모 기본 생성
            RegionTreeDto parent = RegionTreeDto.parent(p.getId(), p.getName());

            // 자식들 매핑 + 정렬(원하면)
            List<RegionTreeDto> children = p.getChildren().stream()
                    .sorted(Comparator.comparing(Region::getId).reversed())
                    .map(c -> RegionTreeDto.child(c.getId(), c.getName(), p.getId()))
                    .toList();

            // 자식 채워서 새 레코드 반환(childCount 자동 반영)
            return parent.withChildren(children);
        }).toList();
    }


    @Transactional
    public void delete(Long id) {
        regionRepository.deleteById(id);
    }
}
