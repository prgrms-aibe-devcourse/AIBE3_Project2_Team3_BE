package com.pi.domain.skill.skill.service;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public long count() {
        return skillRepository.count();
    }

    @Transactional
    public Skill create(SkillCreateReqBody dto) {
        Skill skill = new Skill(dto.name());
        return skillRepository.save(skill);
    }

    @Transactional(readOnly = true)
    public Page<SkillDto> getSkills(Pageable pageable, String searchKeyword) {
        return skillRepository.findByNameContainingIgnoreCase(searchKeyword, pageable)
                .map(SkillDto::new);
    }

    @Transactional
    public void delete(Long id) {
        skillRepository.deleteById(id);
    }
}
