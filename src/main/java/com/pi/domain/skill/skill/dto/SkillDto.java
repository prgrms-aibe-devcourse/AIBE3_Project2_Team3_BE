package com.pi.domain.skill.skill.dto;

import com.pi.domain.skill.skill.entity.Skill;

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

    public SkillDto(String name) {
        this(
                null,
                name
        );
    }
}
