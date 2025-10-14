package com.pi.domain.skill.skill.controller;

import com.pi.domain.skill.skill.service.SkillService;
import com.pi.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/skills")
@Tag(name = "ApiV1AdminSkillController", description = "API 관리자 스킬 컨트롤러")
public class ApiV1AdmSkillController {
    private final SkillService skillService;

    @Operation(summary = "스킬 삭제")
    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return new RsData<>("200-1", "스킬이 삭제되었습니다.");
    }
}
