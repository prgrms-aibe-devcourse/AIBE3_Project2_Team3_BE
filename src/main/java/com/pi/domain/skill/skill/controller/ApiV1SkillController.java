package com.pi.domain.skill.skill.controller;

import com.pi.domain.skill.skill.dto.SkillDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skills")
@Tag(name = "ApiV1SkillController", description = "API 스킬 컨트롤러")
public class ApiV1SkillController {
    private final SkillService skillService;
    @Operation(summary = "전체 스킬 조회")
    @GetMapping
    public PagePayload<SkillDto> getSkills(
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "") String searchKeyword
    ) {
        return Ut.pageMapper.of(skillService.getSkills(pageable, searchKeyword));
    }
}
