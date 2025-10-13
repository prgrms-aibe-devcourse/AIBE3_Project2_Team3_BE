package com.pi.domain.skill.skill.controller;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.service.SkillService;
import com.pi.global.rsData.PagePayload;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/skills")
@Tag(name = "ApiV1AdminSkillController", description = "API 관리자 스킬 컨트롤러")
public class ApiV1AdmSkillController {
    private final SkillService skillService;

    @Operation(summary= "스킬 생성")
    @PostMapping
    public SkillDto createSkill(@RequestBody SkillCreateReqBody dto) {
        Skill skill = skillService.createSkill(dto);
        return SkillDto.from(skill);
    }

    @Operation(summary = "전체 스킬 조회")
    @GetMapping
    public PagePayload<SkillDto> getSkills(
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return Ut.pageMapper.of(skillService.getSkills(pageable));
    }

    @Operation(summary = "스킬 삭제")
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }
}
