package com.pi.domain.admin.skill.skill.dto;

import com.pi.domain.admin.skill.skill.entity.Skill;

public record SkillDto(
        Long id,
        String name
) {
    public SkillDto(Skill skill) {
        this(
                skill.getId(),
                skill.getName()
        );
    }
}
