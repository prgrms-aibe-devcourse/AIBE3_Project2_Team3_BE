package com.pi.domain.category.category.controller;

import com.pi.domain.category.category.dto.CategoryCreateReqBody;
import com.pi.domain.category.category.dto.CategoryTreeDto;
import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "ApiV1AdminCategoryController", description = "API 관리자 카테고리 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1AdmCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성")
    @PostMapping
    public CategoryTreeDto create(@RequestBody CategoryCreateReqBody reqBody) {
        Category c = categoryService.create(reqBody);
        return CategoryTreeDto.child(c.getId(), c.getName(), c.getParent() != null ? c.getParent().getId() : null);
    }

    @Operation(summary = "카테고리 삭제")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
