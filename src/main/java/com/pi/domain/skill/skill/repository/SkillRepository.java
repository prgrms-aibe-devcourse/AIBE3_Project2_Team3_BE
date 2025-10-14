package com.pi.domain.skill.skill.repository;

import com.pi.domain.skill.skill.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Page<Skill> findByNameContainingIgnoreCase(String q, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
