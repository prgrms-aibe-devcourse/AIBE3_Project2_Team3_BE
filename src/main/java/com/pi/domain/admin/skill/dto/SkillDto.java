package com.pi.domain.admin.skill.dto;

import com.pi.domain.admin.skill.entity.Skill;

public record SkillDto(
        Long id,
        String name
) {
    public SkillDto(Skill skill) {
        this(skill.getId(), skill.getName());
    }

    public static SkillDto from(Skill skill) {
        return new SkillDto(skill.getId(), skill.getName());
    }
}
