package com.pi.domain.skill.skill.controller;

import com.pi.domain.skill.skill.dto.SkillCreateReqBody;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public SkillDto create(@RequestBody SkillCreateReqBody reqBody) {
        Skill skill = skillService.create(reqBody);
        return new SkillDto(skill);
    }

    @Operation(summary = "스킬 삭제")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        skillService.delete(id);
    }
}
