package com.pi.domain.admin.skill.service;

import com.pi.domain.admin.skill.dto.SkillCreateReqBody;
import com.pi.domain.admin.skill.dto.SkillDto;
import com.pi.domain.admin.skill.entity.Skill;
import com.pi.domain.admin.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    @Transactional
    public Skill createSkill(SkillCreateReqBody dto) {
        Skill skill = new Skill(dto.name());
        return skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getSkills() {
        return skillRepository.findAll()
                .stream()
                .map(SkillDto::from)
                .toList();
    }

    @Transactional
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }
}
