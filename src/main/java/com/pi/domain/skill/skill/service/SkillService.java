package com.pi.domain.skill.skill.service;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public long count() {
        return skillRepository.count();
    }

    @Transactional
    public Skill create(SkillCreateReqBody dto) {
        // 1) 입력 정규화
        String raw = Optional.ofNullable(dto.name()).orElse("").trim();
        if (raw.isBlank()) {
            throw new ServiceException("400-1", "스킬명을 입력해주세요.");
        }
        String name = raw.replaceAll("\\s+", " "); // 연속 공백 축약

        // 2) 사전 중복 검사(대소문자 무시)
        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new ServiceException("409-1", "이미 존재하는 스킬입니다.");
        }

        // 3) 저장 (동시성으로 인한 레이스 컨디션은 DB 유니크로 한 번 더 잡음)
        try {
            return skillRepository.save(new Skill(name));
        } catch (DataIntegrityViolationException e) {
            // 거의 동시에 같은 이름이 들어온 경우
            throw new ServiceException("409-1", "이미 존재하는 스킬입니다.");
        }
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
