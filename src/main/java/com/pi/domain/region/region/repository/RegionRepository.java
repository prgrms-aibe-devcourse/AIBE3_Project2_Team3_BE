package com.pi.domain.region.region.repository;

import com.pi.domain.region.region.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByParentIsNull();

    Page<Region> findByParentIsNull(Pageable pageable);

    List<Region> findByParentId(Long parentId);
}
